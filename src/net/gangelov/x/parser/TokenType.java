package net.gangelov.x.parser;

public enum TokenType {
    EOF,
    Newline, // \n

    OpenParen, // (
    CloseParen, // )

    OpenBracket, // [
    CloseBracket, // ]

    OpenBrace, // {
    CloseBrace, // }

    Comma, // ,
    Dot, // .
    Colon, // :

    Name,

    Number,
    String,

    If,
    Else,
    Elsif,
    Do,
    While,
    Def,
    End
}
