package net.gangelov.x.parser;

import net.gangelov.x.ast.*;

import java.io.IOException;

public class Parser {
    public class ParserException extends Exception {
        ParserException(String fileName, Token token) {
            super("Unknown token '" + token.type.name() + "' at " + fileName + ":" + token.line + ":" + token.column);
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

    private ASTNode parseExpression(int minPrecedence) throws ParserException, IOException, Lexer.LexerException {
        ASTNode left = parsePrimary();

        while (true) {
            if (t.type != TokenType.BinaryOperator) {
                return left;
            }

            int opPrecedence = operatorPrecedence(t);
            if (opPrecedence < minPrecedence) {
                return left;
            }

            Token op = read();
            ASTNode right = parseExpression(opPrecedence + 1);

            left = new MethodCallNode(op.str, left, right);
        }
    }

    private ASTNode parsePrimary() throws IOException, Lexer.LexerException, ParserException {
        switch (t.type) {
            case Number:        return new NumberLiteralNode(read().str);
            case String:        return new StringLiteralNode(read().str);
            case Name:          return new NameNode(read().str);
            case UnaryOperator: return new MethodCallNode(read().str, parsePrimary());
            case OpenParen:
                read(); // (
                ASTNode node = parseNext();
                if (t.type != TokenType.CloseParen) {
                    break;
                }
                read(); // )

                return node;
        }

        throw new ParserException(lexer.getFileName(), t);
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
}
