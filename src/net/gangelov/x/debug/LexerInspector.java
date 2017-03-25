package net.gangelov.x.debug;

import net.gangelov.x.Lexer;

import java.util.List;
import java.util.stream.Collectors;

public class LexerInspector {
    public static String inspectToken(Lexer.Token token) {
        return "(" + token.type.name() + " '" + token.str + "')";
    }

    public static String inspectTokens(List<Lexer.Token> tokens) {
        return tokens.stream().map(LexerInspector::inspectToken).collect(Collectors.joining(" "));
    }
}
