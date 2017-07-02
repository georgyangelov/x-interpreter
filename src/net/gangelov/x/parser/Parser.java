package net.gangelov.x.parser;

import net.gangelov.x.ast.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    public class ParserException extends Exception {
        ParserException(String fileName, Token token) {
            super(
                    "Unknown token '" +
                    token.type.name() +
                    "' at " + fileName + ":" + token.line + ":" + token.column
            );
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

            if (expression instanceof NumberLiteralNode) {
                NumberLiteralNode number = (NumberLiteralNode)expression;
                number.str = "-" + number.str;

                return number;
            } else {
                return new MethodCallNode("-", expression);
            }
        }

        ASTNode target = parseMethodTarget();

        return maybeParseMethodCall(target);
    }

    private ASTNode parseMethodTarget() throws ParserException, IOException, Lexer.LexerException {
        switch (t.type) {
            case Number:        return new NumberLiteralNode(read().str);
            case String:        return new StringLiteralNode(read().str);
            case Name:          return new NameNode(read().str);
            case UnaryOperator: return new MethodCallNode(read().str, parsePrimary());
            case If:            return parseIf();
            case While:         return parseWhile();
            case Def:           return parseDef();
            case OpenParen:
                read(); // (
                ASTNode node = parseNext();
                if (t.type != TokenType.CloseParen) {
                    break;
                }
                read(); // )

                return node;
        }

        parseError();
        
        return null;
    }

    private MethodDefinitionNode parseDef() throws IOException, Lexer.LexerException, ParserException {
        read(); // def

        if (t.type != TokenType.Name) parseError();
        String name = read().str;

        List<MethodArgumentNode> arguments = parseMethodArguments();

        if (t.type != TokenType.Colon) parseError();
        read(); // :

        if (t.type != TokenType.Name) parseError();
        String returnType = read().str;

        BlockNode body = parseBlock();

        if (t.type != TokenType.End) parseError();
        read(); // end

        return new MethodDefinitionNode(name, returnType, arguments, body);
    }

    private List<MethodArgumentNode> parseMethodArguments()
            throws IOException, Lexer.LexerException, ParserException {
        List<MethodArgumentNode> arguments = new ArrayList<>();

        if (t.type != TokenType.OpenParen) {
            return arguments;
        }

        read(); // (

        while (true) {
            arguments.add(parseMethodArgument());

            if (t.type != TokenType.Comma) {
                break;
            }

            read(); // ,
        }

        if (t.type != TokenType.CloseParen) parseError();
        read(); // )

        return arguments;
    }

    private MethodArgumentNode parseMethodArgument()
            throws IOException, Lexer.LexerException, ParserException {
        if (t.type != TokenType.Name) parseError();
        String name = read().str;

        if (t.type != TokenType.Colon) parseError();
        read(); // :

        if (t.type != TokenType.Name) parseError();
        String type = read().str;

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
        List<ASTNode> nodes = new ArrayList<>();

        while (!blockEnds()) {
            nodes.add(parseNext());
        }

        return new BlockNode(nodes);
    }

    private ASTNode maybeParseMethodCall(ASTNode target)
            throws IOException, Lexer.LexerException, ParserException {
        // target.call
        if (t.type == TokenType.Dot) {
            read(); // .

            if (t.type != TokenType.Name) parseError();
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
               blockEnds();
    }

    private boolean blockEnds() {
        return t.type == TokenType.End ||
               t.type == TokenType.Else ||
               t.type == TokenType.Elsif;
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
