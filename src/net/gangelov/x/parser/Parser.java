package net.gangelov.x.parser;

import net.gangelov.x.ast.*;
import net.gangelov.x.ast.nodes.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    public class ParserException extends Exception {
        public final boolean isIncomplete;

        ParserException(String fileName, Token token) {
            super(
                    "Unknown token '" +
                    token.type.name() +
                    "' at " + fileName + ":" + token.line + ":" + token.column
            );

            this.isIncomplete = token.type == TokenType.EOF;
        }
    }

    private Lexer lexer;
    private Token t = null;
    private boolean newline = false;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public boolean hasMoreTokens() throws IOException, Lexer.LexerException {
        if (t == null) {
            read();
        }

        return t.type != TokenType.EOF;
    }

    public ASTNode parseNext() throws IOException, Lexer.LexerException, ParserException {
        if (t == null) {
            read();
        }

        return parseExpression(0);
    }

    private ASTNode parseExpression(int minPrecedence)
            throws ParserException, IOException, Lexer.LexerException {
        ASTNode left = parsePrimary();

        while (true) {
            if (newline) {
                return left;
            }

            if (t.type != TokenType.BinaryOperator) {
                return left;
            }

            int opPrecedence = operatorPrecedence(t);
            if (opPrecedence < minPrecedence) {
                return left;
            }

            Token op = read();
            ASTNode right = parseExpression(opPrecedence + 1);

            if (op.str.equals("=")) {
                if (!(left instanceof NameNode)) {
                    throw new ParserException("Assignment must have name as left-hand expression", op);
                }

                left = new AssignmentNode(((NameNode)left).name, right);
            } else {
                left = new MethodCallNode(op.str, left, right);
            }
        }
    }

    private ASTNode parsePrimary() throws IOException, Lexer.LexerException, ParserException {
        if (t.type == TokenType.BinaryOperator && t.str.equals("-")) {
            read(); // -

            ASTNode expression = parsePrimary();

            if (
                    expression instanceof LiteralNode && (
                        ((LiteralNode) expression).type == LiteralNode.Type.Int ||
                        ((LiteralNode) expression).type == LiteralNode.Type.Float
                    )
            ) {
                LiteralNode number = (LiteralNode)expression;
                number.str = "-" + number.str;

                return number;
            } else {
                return new MethodCallNode("-", expression);
            }
        }

        ASTNode target = parseMethodTarget();

        do {
            target = maybeParseMethodCall(target);
        } while (t.type == TokenType.Dot);

        return target;
    }

    private ASTNode parseMethodTarget() throws ParserException, IOException, Lexer.LexerException {
        switch (t.type) {
            case Nil:           return new LiteralNode(LiteralNode.Type.Nil, read().str);
            case Bool:          return new LiteralNode(LiteralNode.Type.Bool, read().str);
            case Number:
                if (t.str.contains(".")) {
                    return new LiteralNode(LiteralNode.Type.Float, read().str);
                } else {
                    return new LiteralNode(LiteralNode.Type.Int, read().str);
                }
            case String:        return new LiteralNode(LiteralNode.Type.String, read().str);
            case Name:          return new NameNode(read().str);
            case UnaryOperator: return new MethodCallNode(read().str, parsePrimary());
            case If:            return parseIf();
            case While:         return parseWhile();
            case Def:           return parseDef();
            case Class:         return parseClass();
            case Begin:         return parseStandaloneBlock();
            case Do:            return parseMultilineLambda();

            case OpenParen:
                read(); // (
                ASTNode node = parseNext();
                if (t.type != TokenType.CloseParen) parseError();
                read(); // )

                return node;
        }

        parseError();
        
        return null;
    }

    private LambdaNode parseMultilineLambda() throws IOException, Lexer.LexerException, ParserException {
        read(); // do

        List<MethodArgumentNode> arguments;
        if (t.type == TokenType.Pipe) {
            read(); // (

            arguments = parseMethodArguments();

            if (t.type != TokenType.Pipe) parseError();
            read(); // )
        } else {
            arguments = new ArrayList<>();
        }

        BlockNode block = parseBlock();

        if (t.type != TokenType.End) parseError();
        read(); // end

        return new LambdaNode(arguments, block);
    }

    private BlockNode parseStandaloneBlock() throws IOException, Lexer.LexerException, ParserException {
        read(); // do

        BlockNode block = parseBlock();

        if (t.type != TokenType.End) parseError();
        read(); // end

        return block;
    }

    private ClassDefinitionNode parseClass() throws IOException, Lexer.LexerException, ParserException {
        read(); // class

        if (t.type != TokenType.Name) parseError();
        String name = read().str;
        String superclass = "Object";

        if (t.type == TokenType.BinaryOperator && t.str.equals("<")) {
            read(); // <

            if (t.type != TokenType.Name) parseError();
            superclass = read().str;
        }

        BlockNode body = parseBlock();

        if (t.type != TokenType.End) parseError();
        read(); // end

        return new ClassDefinitionNode(name, superclass, body);
    }

    private MethodDefinitionNode parseDef() throws IOException, Lexer.LexerException, ParserException {
        read(); // def

        if (t.type != TokenType.Name) parseError();
        String name = read().str;

        List<MethodArgumentNode> arguments;
        if (t.type == TokenType.OpenParen) {
            read(); // (

            arguments = parseMethodArguments();

            if (t.type != TokenType.CloseParen) parseError();
            read(); // )
        } else {
            arguments = new ArrayList<>();
        }

        String returnType = null;
        if (t.type == TokenType.Colon) {
            read(); // :

            if (t.type != TokenType.Name) parseError();
            returnType = read().str;
        }

        BlockNode body = parseBlock();

        if (t.type != TokenType.End) parseError();
        read(); // end

        return new MethodDefinitionNode(name, returnType, arguments, body);
    }

    private List<MethodArgumentNode> parseMethodArguments()
            throws IOException, Lexer.LexerException, ParserException {
        List<MethodArgumentNode> arguments = new ArrayList<>();

        while (true) {
            arguments.add(parseMethodArgument());

            if (t.type != TokenType.Comma) {
                break;
            }

            read(); // ,
        }

        return arguments;
    }

    private MethodArgumentNode parseMethodArgument()
            throws IOException, Lexer.LexerException, ParserException {
        if (t.type != TokenType.Name) parseError();
        String name = read().str;

        String type = null;
        if (t.type == TokenType.Colon) {
            read(); // :

            if (t.type != TokenType.Name) parseError();
            type = read().str;
        }

        return new MethodArgumentNode(name, type);
    }

    private WhileNode parseWhile() throws IOException, Lexer.LexerException, ParserException {
        read(); // while

        ASTNode condition = parseNext();

        if (!newline) parseError();

        BlockNode body = parseBlock();

        if (t.type != TokenType.End) parseError();
        read(); // end

        return new WhileNode(condition, body);
    }

    private BranchNode parseIf() throws IOException, Lexer.LexerException, ParserException {
        read(); // `if` or `elsif`

        ASTNode condition = parseNext();

        if (!newline) parseError();

        BlockNode true_branch = parseBlock();
        BlockNode false_branch = new BlockNode();

        if (t.type == TokenType.Else) {
            read(); // else
            false_branch = parseBlock();

            if (t.type != TokenType.End) parseError();
            read(); // end
        } else if (t.type == TokenType.Elsif) {
            false_branch.nodes.add(parseIf());
        } else {
            if (t.type != TokenType.End) parseError();
            read(); // end
        }

        return new BranchNode(condition, true_branch, false_branch);
    }

    private BlockNode parseBlock() throws ParserException, IOException, Lexer.LexerException {
        return parseBlock(false);
    }

    private BlockNode parseBlock(boolean skipCatch)
            throws ParserException, IOException, Lexer.LexerException {
        List<ASTNode> nodes = new ArrayList<>();
        List<CatchNode> catches = new ArrayList<>();

        while (!blockEnds()) {
            nodes.add(parseNext());
        }

        if (!skipCatch) {
            while (t.type == TokenType.Catch) {
                catches.add(parseCatch());
            }
        }

        return new BlockNode(nodes, catches);
    }

    private CatchNode parseCatch() throws ParserException, IOException, Lexer.LexerException {
        read(); // catch

        String name = null, klass = "Error";

        if (!newline) {
            if (t.type == TokenType.Name && !isConstName(t.str)) {
                name = read().str;

                if (t.type == TokenType.Colon) {
                    read(); // :

                    if (t.type != TokenType.Name) parseError();
                    klass = read().str;
                }
            } else if (t.type == TokenType.Name) {
                klass = read().str;
            }
        }

        BlockNode body = parseBlock(true);

        return new CatchNode(klass, name, body);
    }

    private ASTNode maybeParseMethodCall(ASTNode target)
            throws IOException, Lexer.LexerException, ParserException {
        // target.call
        if (t.type == TokenType.Dot) {
            read(); // .

            if (!isMethodName(t)) parseError();
            Token name = read();

            List<ASTNode> arguments = parseArguments();

            return new MethodCallNode(name.str, target, arguments);
        }

        // target a
        if (target instanceof NameNode && !currentExpressionMayEnd()) {
            List<ASTNode> arguments = parseArguments();

            return new MethodCallNode(((NameNode)target).name, new NameNode("self"), arguments);
        }

        return target;
    }

    private List<ASTNode> parseArguments() throws ParserException, IOException, Lexer.LexerException {
        List<ASTNode> arguments = new ArrayList<>();
        boolean withParens = false;

        if (t.type == TokenType.OpenParen) {
            read(); // (
            withParens = true;
        }

        if (!withParens && currentExpressionMayEnd()) {
            return arguments;
        }

        if (withParens && t.type == TokenType.CloseParen) {
            read(); // )
            return arguments;
        }

        while (true) {
            arguments.add(parseNext());

            if (t.type != TokenType.Comma) {
                break;
            }

            read(); // ,
        }

        if (withParens) {
            if (t.type != TokenType.CloseParen) parseError();
            read(); // )
        } else if (!currentExpressionMayEnd()) {
            parseError();
        }

        return arguments;
    }

    private int operatorPrecedence(Token op) throws ParserException {
        switch (op.str) {
            case "=": return 1;
            case "or": return 2;
            case "and": return 3;
            case "==":case "<":case ">":case "<=":case ">=":case "!=": return 4;
            case "+":case "-": return 5;
            case "*":case "/": return 6;
            default: throw new ParserException(lexer.getFileName(), op);
        }
    }

    private boolean currentExpressionMayEnd() {
        return newline ||
               t.type == TokenType.EOF ||
               t.type == TokenType.BinaryOperator ||
               t.type == TokenType.Comma ||
               t.type == TokenType.CloseParen ||
               t.type == TokenType.Dot ||
               blockEnds();
    }

    private boolean blockEnds() {
        return t.type == TokenType.End ||
               t.type == TokenType.Else ||
               t.type == TokenType.Elsif ||
               t.type == TokenType.Catch;
    }

    private boolean isMethodName(Token t) {
        return t.type == TokenType.Name || t.type == TokenType.Class;
    }

    private boolean isConstName(String name) {
        return Character.isUpperCase(name.codePointAt(0));
    }

    private Token read() throws IOException, Lexer.LexerException {
        Token old = t;

        newline = false;

        t = lexer.nextToken();
        while (t.type == TokenType.Newline) {
            newline = true;
            t = lexer.nextToken();
        }

        return old;
    }

    private void parseError() throws ParserException {
        throw new ParserException(lexer.getFileName(), t);
    }
}
