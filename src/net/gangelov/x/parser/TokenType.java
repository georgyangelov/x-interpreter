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

    Pipe, // |

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
    While,
    Def,
    End,
    Class,
    Begin,
    Catch,
    Do
}
