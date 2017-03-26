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
        assertEquals("12345", parse("12345 "));
        assertEquals("1234.5678", parse("1234.5678"));
        assertEquals("-1234", parse("-1234"));
        assertEquals("-1234.5", parse("-1234.5"));
    }

    @Test
    void testStringLiteral() throws Exception {
        assertEquals("\"Hello world!\"", parse("\"Hello world!\""));
        assertEquals("\"\\\"\\n\"", parse("\"\\\"\\n\""));
    }

    @Test
    void testIf() throws Exception {
        assertEquals("(if (name a) { (name b) (name c) })", parse("if a \n b c end"));
        assertEquals("(if (name a) { (name b) (name c) } { (name d) (name e) })", parse("if a \n b c else d e end"));
        assertEquals("(if 1 { 2 } (if 3 { 4 }))", parse("if 1\n 2\nelsif 3\n4\nend"));
        assertEquals("(if 1 { 2 } (if 3 { 4 } { 5 }))", parse("if 1\n 2\nelsif 3\n4\nelse\n5\nend"));
    }

    @Test
    void testWhile() throws Exception {
        assertEquals("(while (name a) { (name b) (name c) }", parse("while a\n b c end"));
    }

    @Test
    void testInfixOperators() throws Exception {
        assertEquals("(+ (+ (+ 1 2) 3) 4)", parse("1 + 2 + 3 + 4"));
        assertEquals("(* (* (* 1 2) 3) 4)", parse("1 * 2 * 3 * 4"));
        assertEquals("(- (- (- 1 2) 3) 4)", parse("1 - 2 - 3 - 4"));
        assertEquals("(/ (/ (/ 1 2) 3) 4)", parse("1 / 2 / 3 / 4"));
        assertEquals("(= (name a) 15)", parse("a = 15"));
    }

    @Test
    void testPrefixOperators() throws Exception {
        assertEquals("(! (name a))", parse("!a"));
        assertEquals("(! (! (name a))", parse("!!a"));
    }

    @Test
    void testOperatorPrecedence() throws Exception {
        assertEquals("(+ 1 (* 2 3))", parse("1 + 2 * 3"));
        assertEquals("(+ (- 1 (* (/ 2 3) 4)) 5)", parse("1 - 2 / 3 * 4 + 5"));
        assertEquals("(* (+ 1 2) 3)", parse("(1 + 2) * 3"));
        assertEquals("(+ 1 (* 2 (! (name a))))", parse("1 + 2 * !a"));
        assertEquals("(= a (* 5 5))", parse("a = 5 * 5"));
        assertEquals("(= a (* 5 5))", parse("a =\n\n 5 * 5"));
        assertEquals("(+ 1 (* 2 3))", parse("1 +\n\n 2 \n\n * \n\n 3"));
    }

    @Test
    void testMethodCalls() throws Exception {
        assertEquals("(name method)", parse("method"));
        assertEquals("(method self)", parse("method()"));
        assertEquals("(method self a b c)", parse("method(a, b, c)"));
        assertEquals("(method self a b c)", parse("method a, b, c"));
        assertEquals("(method self a b c)", parse("method a, \n\n b,\n c"));
        assertEquals("(method target)", parse("target.method"));
        assertEquals("(method target)", parse("target.method()"));
        assertEquals("(method target a b c)", parse("target.method(a, b, c)"));
        assertEquals("(method target a b c)", parse("target.method a, b, c"));
    }

    @Test
    void testMethodCallOnLiterals() throws Exception {
        assertEquals("(abs 1234)", parse("1234.abs"));
    }

    @Test
    void testMethodDefinitions() throws Exception {
        assertEquals("(def (name method) Int [] { (name a) (name b) })", parse("def method:Int\na b\n end"));
        assertEquals("(def (name method) Int [(argument a Int) (argument b String)] { (name a) (name b) })", parse("def method(a:Int, b:String):Int\na b\n end"));
    }

    @Test
    void testThrowsExceptionOnParseError() throws Exception {
        assertThrows(Lexer.LexerException.class, () -> parse("1234 $ 4321"));
        assertThrows(Parser.ParserException.class, () -> parse("if end"));
    }

    private String parse(String source) throws Parser.ParserException, IOException, Lexer.LexerException {
        List<ASTNode> nodes = ParserSupport.parseAll(source);

        return nodes.stream()
            .map(ASTInspector::inspect)
            .collect(Collectors.joining(" "));
    }
}
