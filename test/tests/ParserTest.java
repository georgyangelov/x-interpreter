package tests;

import net.gangelov.x.ast.ASTNode;
import net.gangelov.x.debug.ASTInspector;
import net.gangelov.x.parser.Lexer;
import net.gangelov.x.parser.Parser;
import org.junit.jupiter.api.Test;
import tests.support.ParserSupport;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {
    @Test
    void testNumberLiteral() throws Exception {
        assertEquals(
            "12345",
            parse("12345 ")
        );
    }

    @Test
    void testStringLiteral() throws Exception {
        assertEquals(
            "\"Hello world!\"",
            parse("\"Hello world!\"")
        );
    }

    @Test
    void testStringEscapeSequencePrinting() throws Exception {
        assertEquals(
            "\"\\\"\\n\"",
            parse("\"\\\"\\n\"")
        );
    }

    private String parse(String source) throws Parser.ParserException, IOException, Lexer.LexerException {
        List<ASTNode> nodes = ParserSupport.parseAll(source);

        return nodes.stream()
            .map(ASTInspector::inspect)
            .collect(Collectors.joining(" "));
    }
}
