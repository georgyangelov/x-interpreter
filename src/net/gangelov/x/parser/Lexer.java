package net.gangelov.x.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Stack;

public class Lexer {
    public class LexerException extends Exception {
        public LexerException(String fileName, int line, int column, String c) {
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

        if (Character.isDigit(c) || c == '-') {
            StringBuilder str = new StringBuilder();

            if (c == '-') {
                str.appendCodePoint(next());

                skipWhitespaceAndComments();

                if (!Character.isDigit(c)) {
                    // TODO: Consider it an operator
                    throw new LexerException(fileName, line, column, Character.toString((char) c));
                }
            }

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

        if (Character.isLetter(c) || c == '_') {
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
                case "while":
                    t.type = TokenType.While;
                    break;
                case "def":
                    t.type = TokenType.Def;
                    break;
                default:
                    t.type = TokenType.Name;
                    break;
            }

            return t;
        }

        throw new LexerException(fileName, line, column, Character.toString((char) c));
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
        putBackChars.push(codePoint);
    }
}
