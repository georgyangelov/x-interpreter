package net.gangelov.x.debug;

import net.gangelov.x.parser.Lexer;
import net.gangelov.x.parser.Token;

import java.util.List;
import java.util.stream.Collectors;

public class LexerInspector {
    public static String inspectToken(Token token) {
        return "(" + token.type.name() + " '" + token.str + "')";
    }

    public static String inspectTokens(List<Token> tokens) {
        return tokens.stream().map(LexerInspector::inspectToken).collect(Collectors.joining(" "));
    }
}
