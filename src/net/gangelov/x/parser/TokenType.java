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

    UnaryOperator,
    BinaryOperator,

    Name,

    Nil,
    Number,
    String,
    Bool,

    If,
    Else,
    Elsif,
    Do,
    While,
    Def,
    End,
    Class,
    Catch
}
