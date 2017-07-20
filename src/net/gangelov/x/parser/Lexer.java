package net.gangelov.x.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Stack;
import java.util.function.BinaryOperator;

public class Lexer {
    public class LexerException extends Exception {
        LexerException(String fileName, int line, int column, String c) {
            super("Unknown token '" + c + "' at " + fileName + ":" + line + ":" + column);
        }
    }

    private String fileName;
    private BufferedReader in;
    private int c = -2, line = 1, column = 0;
    private Stack<Integer> putBackChars = new Stack<>();

    public Lexer(String fileName, InputStream in) {
        this.fileName = fileName;
        this.in = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
    }

    public String getFileName() {
        return fileName;
    }

    public Token nextToken() throws IOException, LexerException {
        if (c == -2) {
            next();
        }

        Token t = new Token();
        t.line = line;
        t.column = column;

        skipWhitespaceAndComments();

        switch (c) {
            case -1: return t.set(TokenType.EOF, "EOF");

            case '\n': next(); return t.set(TokenType.Newline,      "\n");
            case '(':  next(); return t.set(TokenType.OpenParen,    "(");
            case ')':  next(); return t.set(TokenType.CloseParen,   ")");
            case '[':  next(); return t.set(TokenType.OpenBracket,  "[");
            case ']':  next(); return t.set(TokenType.CloseBracket, "]");
            case '{':  next(); return t.set(TokenType.OpenBrace,    "{");
            case '}':  next(); return t.set(TokenType.CloseBrace,   "}");
            case ',':  next(); return t.set(TokenType.Comma,        ",");
            case '.':  next(); return t.set(TokenType.Dot,          ".");
            case ':':  next(); return t.set(TokenType.Colon,        ":");
        }

        if (c == '!') {
            next(); // !

            if (c == '=') {
                next();
                return t.set(TokenType.BinaryOperator, "!=");
            }

            return t.set(TokenType.UnaryOperator, "!");
        }

        if (c == '=' || c == '+' || c == '-' || c == '*' || c == '/' || c == '<' || c == '>') {
            StringBuilder str = new StringBuilder(2);
            str.appendCodePoint(next());

            if (c == '=') {
                str.appendCodePoint(next());
                return t.set(TokenType.BinaryOperator, str.toString());
            }

            return t.set(TokenType.BinaryOperator, str.toString());
        }

        if (Character.isDigit(c)) {
            return t.set(TokenType.Number, readNumber());
        }

        if (c == '"') {
            StringBuilder str = new StringBuilder();
            boolean inEscapeSequence = false;
            next(); // "

            while (inEscapeSequence || c != '"') {
                if (inEscapeSequence) {
                    inEscapeSequence = false;
                    str.appendCodePoint(EscapeSequences.fromSequenceChar(next()));
                } else if (c == '\\') {
                    inEscapeSequence = true;
                    next();
                } else {
                    str.appendCodePoint(next());
                }
            }

            next(); // "

            t.str = str.toString();
            t.type = TokenType.String;
            return t;
        }

        if (Character.isLetter(c) || c == '_' || c == '@') {
            StringBuilder str = new StringBuilder();
            str.appendCodePoint(next());

            while (Character.isLetterOrDigit(c) || c == '_' || c == '@') {
                str.appendCodePoint(next());
            }

            if (c == '!' || c == '?') {
                str.appendCodePoint(next());
            }

            t.str = str.toString();

            switch (t.str) {
                case "do":
                    t.type = TokenType.Do;
                    break;
                case "if":
                    t.type = TokenType.If;
                    break;
                case "else":
                    t.type = TokenType.Else;
                    break;
                case "elsif":
                    t.type = TokenType.Elsif;
                    break;
                case "end":
                    t.type = TokenType.End;
                    break;
                case "while":
                    t.type = TokenType.While;
                    break;
                case "def":
                    t.type = TokenType.Def;
                    break;
                case "and":
                    t.type = TokenType.BinaryOperator;
                    break;
                case "or":
                    t.type = TokenType.BinaryOperator;
                    break;
                case "nil":
                    t.type = TokenType.Nil;
                    break;
                case "true": case "false":
                    t.type = TokenType.Bool;
                    break;
                case "class":
                    t.type = TokenType.Class;
                    break;
                default:
                    t.type = TokenType.Name;
                    break;
            }

            return t;
        }

        throw new LexerException(fileName, line, column, Character.toString((char) c));
    }

    private String readNumber() throws IOException {
        StringBuilder str = new StringBuilder();

        while (Character.isDigit(c)) {
            str.appendCodePoint(next());
        }

        if (c == '.') {
            next(); // .

            if (Character.isDigit(c)) {
                str.appendCodePoint('.');

                while (Character.isDigit(c)) {
                    str.appendCodePoint(next());
                }
            } else {
                putBack('.');
            }
        }

        return str.toString();
    }

    private void skipWhitespaceAndComments() throws IOException {
        boolean inComment = false;

        while (true) {
            if (c == -1) {
                break;
            }

            if (inComment) {
                if (c == '\n') {
                    inComment = false;
                }
                next();
            } else {
                if (Character.isWhitespace(c) && c != '\n') {
                    next();
                } else if (c == '#') {
                    next(); // #
                    inComment = true;
                } else {
                    break;
                }
            }
        }
    }

    private int next() throws IOException {
        int old = c;

        if (putBackChars.isEmpty()) {
            c = in.read();
        } else {
            c = putBackChars.pop();
        }

        if (c == '\n') {
            line++;
            column = 0;
        } else {
            column++;
        }

        return old;
    }

    private void putBack(int codePoint) {
        putBackChars.push(c);

        c = codePoint;
    }
}
