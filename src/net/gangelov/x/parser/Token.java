package net.gangelov.x.parser;

public class Token {
    public int line, column;
    public TokenType type;
    public String str = "";

    Token set(TokenType type, String str) {
        this.type = type;
        this.str = str;

        return this;
    }
}
