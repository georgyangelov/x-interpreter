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
    void testNumberLiterals() throws Exception {
        assertEquals("12345", parse("12345 "));
        assertEquals("1234.5678", parse("1234.5678"));
    }

    @Test
    void testNegativeNumberLiterals() throws Exception {
        assertEquals("-1234", parse("-1234"));
        assertEquals("-1234.5", parse("-1234.5"));
    }

    @Test
    void testNegatingExpressions() throws Exception {
        assertEquals("(- test)", parse("-test"));
        assertEquals("(+ -5 5)", parse("-   5 + 5"));
        assertEquals("(- (+ 5 5))", parse("-   (5 + 5)"));
    }

    @Test
    void testStringLiterals() throws Exception {
        assertEquals("\"Hello world!\"", parse("\"Hello world!\""));
        assertEquals("\"\\\"\\n\"", parse("\"\\\"\\n\""));
    }

    @Test
    void testSimpleIfs() throws Exception {
        assertEquals("(if a { (b self c) })", parse("if a \n b c end"));
        assertEquals("(if a { b c })", parse("if a \n b\n c end"));
        assertEquals("(if a { b } { d })", parse("if a \n b else d end"));
        assertEquals("(if a { b c } { d e })", parse("if a \n b \n c else d \n e end"));
        assertEquals("(if a { (test b) })", parse("if a \n b.test end"));
    }

    @Test
    void testElseIf() throws Exception {
        assertEquals("(if 1 { 2 } { (if 3 { 4 }) })", parse("if 1\n 2\nelsif 3\n4\nend"));
        assertEquals("(if 1 { 2 } { (if 3 { 4 } { 5 }) })", parse("if 1\n 2\nelsif 3\n4\nelse\n5\nend"));
    }

    @Test
    void testWhile() throws Exception {
        assertEquals("(while a { b c })", parse("while a\n b\n c end"));
        assertEquals("(while a { (b self c) })", parse("while a\n b c end"));
        assertEquals("(while a { (c b) })", parse("while a\n b.c end"));
    }

    @Test
    void testInfixOperators() throws Exception {
        assertEquals("(+ (+ (+ 1 2) 3) 4)", parse("1 + 2 + 3 + 4"));
        assertEquals("(* (* (* 1 2) 3) 4)", parse("1 * 2 * 3 * 4"));
        assertEquals("(- (- (- 1 2) 3) 4)", parse("1 - 2 - 3 - 4"));
        assertEquals("(/ (/ (/ 1 2) 3) 4)", parse("1 / 2 / 3 / 4"));
    }

    @Test
    void testAssignment() throws Exception {
        assertEquals("(= a 15)", parse("a = 15"));
        assertEquals("(= a (* 5 5))", parse("a = 5 * 5"));

        assertThrows(Parser.ParserException.class, () -> parse("3 = 5"));
        assertThrows(Parser.ParserException.class, () -> parse("a + b = 5"));
    }

    @Test
    void testPrefixOperators() throws Exception {
        assertEquals("(! a)", parse("!a"));
        assertEquals("(! (! a))", parse("!!a"));
    }

    @Test
    void testOperatorPrecedence() throws Exception {
        assertEquals("(+ 1 (* 2 3))", parse("1 + 2 * 3"));

        assertEquals("(== 1 (+ 2 (* 2 3)))", parse("1 == 2 + 2 * 3"));
        assertEquals("(== 1 (+ (* 2 2) 3))", parse("1 == 2 * 2 + 3"));
        assertEquals("(!= 1 (+ (* 2 2) 3))", parse("1 != 2 * 2 + 3"));
        assertEquals("(<= 1 (+ (* 2 2) 3))", parse("1 <= 2 * 2 + 3"));
        assertEquals("(>= 1 (+ (* 2 2) 3))", parse("1 >= 2 * 2 + 3"));
        assertEquals("(< 1 (+ (* 2 2) 3))", parse("1 < 2 * 2 + 3"));
        assertEquals("(> 1 (+ (* 2 2) 3))", parse("1 > 2 * 2 + 3"));

        assertEquals("(+ (- 1 (* (/ 2 3) 4)) 5)", parse("1 - 2 / 3 * 4 + 5"));
    }

    @Test
    void testParensForPrecedence() throws Exception {
        assertEquals("(* (+ 1 2) 3)", parse("(1 + 2) * 3"));
    }

    @Test
    void testUnaryOperatorPrecedence() throws Exception {
        assertEquals("(+ 1 (* 2 (! a)))", parse("1 + 2 * !a"));
        assertEquals("(+ (! 1) (* 2 a))", parse("!1 + 2 * a"));
    }

    @Test
    void testNewlinesInExpressions() throws Exception {
        assertEquals("(= a (* 5 5))", parse("a =\n\n 5 * 5"));

        assertEquals("(- (+ 1 2) 5)", parse("1 + 2 - 5"));
        assertEquals("(+ 1 2) -5", parse("1 + 2 \n - 5"));

        assertEquals("(+ 1 (* 2 3))", parse("1 +\n\n 2 * \n\n 3"));
        assertThrows(Parser.ParserException.class, () -> parse("1 +\n\n 2 \n * \n\n 3"));
    }

    @Test
    void testNames() throws Exception {
        assertEquals("test test_two test3", parse("test \n test_two \n test3"));
    }

    @Test
    void testMethodCalls() throws Exception {
        assertEquals("method", parse("method"));
        assertEquals("(method self)", parse("method()"));
        assertEquals("(method target)", parse("target.method"));
        assertEquals("(method target)", parse("target.method()"));
    }

    @Test
    void testMethodCallsWithArguments() throws Exception {
        assertEquals("(method self a)", parse("method(a)"));
        assertEquals("(method self a)", parse("method a"));

        assertEquals("(method self a b c)", parse("method(a, b, c)"));
        assertEquals("(method self a b c)", parse("method a, b, c"));

        assertEquals("(method self a b c)", parse("method a, \n\n b,\n c"));

        assertEquals("(method one a b c) (d two)", parse("one.method a, \n\n b,\n c\n two.d"));

        assertEquals("(method target a)", parse("target.method(a)"));
        assertEquals("(method target a)", parse("target.method a"));

        assertEquals("(method target a b c)", parse("target.method(a, b, c)"));
        assertEquals("(method target a b c)", parse("target.method a, b, c"));
    }

    @Test
    void testArgumentAssociativity() throws Exception {
        assertEquals("(one self (two self a b))", parse("one two a, b"));
        assertEquals("(one self (two self a) b)", parse("one two(a), b"));
        assertEquals("(one self (two self a b))", parse("one(two a, b)"));
        assertEquals("(one self (two self a b) c d)", parse("one two(a, b), c, d"));

        assertEquals("(method self a b c) d", parse("method a, \n\n b,\n c\n d"));
        assertEquals("(method self a b c) d", parse("method(a, \n\n b,\n c\n) d"));
        assertEquals("(method self a b (c self (d self (e self f))))", parse("method a, \n\n b,\n c d e f"));
    }

    @Test
    void testMethodCallPriority() throws Exception {
        assertEquals("(one self (+ a b))", parse("one a + b"));
        assertEquals("(+ (one self a) b)", parse("one(a) + b"));

        assertEquals("(one self a (+ b c))", parse("one a, b + c"));
        assertEquals("(+ (one self a b) c)", parse("one(a, b) + c"));
    }

    @Test
    void testMethodCallLineCompleteness() throws Exception {
        assertThrows(Parser.ParserException.class, () -> parse("call 1234 b"));
        assertThrows(Parser.ParserException.class, () -> parse("call \"test\" b"));
    }

    @Test
    void testMethodCallOnLiterals() throws Exception {
        assertEquals("(abs 1234)", parse("1234.abs"));
    }

    @Test
    void testSimpleMethodDefinitions() throws Exception {
        assertEquals("(def method:Int [] { a })", parse("def method:Int\na end"));
        assertEquals("(def method:Int [] { a b })", parse("def method:Int\na \n b end"));
        assertEquals("(def method:Int [] { (= a b) (+ a b) })", parse("def method:Int\n a = b\n a + b end"));
    }

    @Test
    void testMethodDefinitionsWithArguments() throws Exception {
        assertEquals("(def method:Int [(argument a:Int) (argument b:String)] { a b })",
                parse("def method(a:Int, b:String):Int\na \n b\n end"));
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
