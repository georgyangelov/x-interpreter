package net.gangelov.x.parser;

import net.gangelov.x.ast.ASTNode;
import net.gangelov.x.ast.NumberLiteralNode;
import net.gangelov.x.ast.StringLiteralNode;

import java.io.IOException;

public class Parser {
    public class ParserException extends Exception {
        public ParserException(String fileName, Token token) {
            super("Unknown token '" + token.type.name() + "' at " + fileName + ":" + token.line + ":" + token.column);
        }
    }

    private Lexer lexer;
    private Token t = null;

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

        if (t.type == TokenType.Number) {
            return new NumberLiteralNode(read().str);
        } else if (t.type == TokenType.String) {
            return new StringLiteralNode(read().str);
        }

        throw new ParserException(lexer.getFileName(), t);
    }

    private Token read() throws IOException, Lexer.LexerException {
        Token old = t;

        t = lexer.nextToken();

        return old;
    }
}
