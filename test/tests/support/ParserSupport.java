package tests.support;

import net.gangelov.x.ast.ASTNode;
import net.gangelov.x.parser.Lexer;
import net.gangelov.x.parser.Parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class ParserSupport {
    public static Lexer lexerForString(String str) {
        InputStream input = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));

        return new Lexer("test.x", input);
    }

    public static Parser parserForString(String str) {
        return new Parser(lexerForString(str));
    }

    public static List<ASTNode> parseAll(String str) throws IOException, Lexer.LexerException, Parser.ParserException {
        Parser p = parserForString(str);
        List<ASTNode> nodes = new ArrayList<>();

        while (p.hasMoreTokens()) {
            nodes.add(p.parseNext());
        }

        return nodes;
    }
}
