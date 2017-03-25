package net.gangelov.x.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Lexer {
    public class LexerException extends Exception {
        public LexerException(String fileName, int line, int column, int c) {
            super("Unknown token '" + c + "' at " + fileName + ":" + line + ":" + column);
        }
    }

    private String fileName;
    private BufferedReader in;
    private int c = -2, line = 1, column = 0;

    public Lexer(String fileName, InputStream in) {
        this.fileName = fileName;
        this.in = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
    }

    public Token nextToken() throws IOException, LexerException {
        if (c == -2) {
            next();
        }

        Token t = new Token();
        t.line = line;
        t.column = column;

        while (Character.isWhitespace(c) && c != '\n') {
            next();
        }

        if (c == -1) { return t.set(TokenType.EOF, "EOF"); }

        if (c == '\n') { next(); return t.set(TokenType.Newline,      "\n"); }
        if (c == '(')  { next(); return t.set(TokenType.OpenParen,    "(");  }
        if (c == ')')  { next(); return t.set(TokenType.CloseParen,   ")");  }
        if (c == '[')  { next(); return t.set(TokenType.OpenBracket,  "[");  }
        if (c == ']')  { next(); return t.set(TokenType.CloseBracket, "]");  }
        if (c == '{')  { next(); return t.set(TokenType.OpenBrace,    "{");  }
        if (c == '}')  { next(); return t.set(TokenType.CloseBrace,   "}");  }
        if (c == ',')  { next(); return t.set(TokenType.Comma,        ",");  }
        if (c == '.')  { next(); return t.set(TokenType.Dot,          ".");  }
        if (c == ':')  { next(); return t.set(TokenType.Colon,        ":");  }

        if (Character.isDigit(c)) {
            StringBuilder str = new StringBuilder();
            while (Character.isDigit(c)) {
                 str.appendCodePoint(next());
            }

            t.str = str.toString();
            t.type = TokenType.Number;
            return t;
        }

        if (c == '"') {
            StringBuilder str = new StringBuilder();
            boolean inEscapeSequence = false;
            next(); // "

            while (inEscapeSequence || c != '"') {
                if (inEscapeSequence) {
                    inEscapeSequence = false;
                    str.appendCodePoint(escapeSequence(next()));
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

        if (Character.isLetter(c)) {
            StringBuilder str = new StringBuilder();
            str.appendCodePoint(next());

            while (Character.isLetterOrDigit(c) || c == '_') {
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
                default:
                    t.type = TokenType.Name;
                    break;
            }

            return t;
        }

        throw new LexerException(fileName, line, column, c);
    }

    private int escapeSequence(int c) {
        if (c == 'n') {
            return '\n';
        } else if (c == 't') {
            return '\t';
        }

        return c;
    }

    private int next() throws IOException {
        int old = c;

        c = in.read();

        if (c == '\n') {
            line++;
            column = 0;
        } else {
            column++;
        }

        return old;
    }
}
