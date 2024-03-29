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
    void testConstantObjectLiterals() throws Exception {
        assertEquals("true", parse("true"));
        assertEquals("false", parse("false"));
        assertEquals("nil", parse("nil"));

        assertThrows(Parser.ParserException.class, () -> parse("true = 5"));
        assertThrows(Parser.ParserException.class, () -> parse("false = 5"));
        assertThrows(Parser.ParserException.class, () -> parse("nil = 5"));
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
        assertEquals("(while (< a 1) { b })", parse("while a < 1\n b end"));
    }

    @Test
    void testInfixOperators() throws Exception {
        assertEquals("(+ (+ (+ 1 2) 3) 4)", parse("1 + 2 + 3 + 4"));
        assertEquals("(* (* (* 1 2) 3) 4)", parse("1 * 2 * 3 * 4"));
        assertEquals("(- (- (- 1 2) 3) 4)", parse("1 - 2 - 3 - 4"));
        assertEquals("(/ (/ (/ 1 2) 3) 4)", parse("1 / 2 / 3 / 4"));

        assertEquals("(or (or 1 (and 2 3)) 4)", parse("1 or 2 and 3 or 4"));
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
        assertEquals("@test @test_two @test3", parse("@test \n @test_two \n @test3"));
    }

    @Test
    void testMethodCalls() throws Exception {
        assertEquals("method", parse("method"));
        assertEquals("(method self)", parse("method()"));
        assertEquals("(method target)", parse("target.method"));
        assertEquals("(method target)", parse("target.method()"));

        assertEquals("([] a)", parse("a[]"));
        assertEquals("(a self (new Array 1))", parse("a [1]"));
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

        assertEquals("(puts self (new Array 1 2 3))", parse("puts [1, 2, 3]"));

        assertEquals("([] a b c)", parse("a[b, c]"));
    }

    @Test
    void testMethodChaining() throws Exception {
        assertEquals("(c (b a))", parse("a.b.c"));
        assertEquals("(d (c (b a)) e)", parse("a.b.c.d e"));
        assertEquals("(c (b a 1))", parse("a.b(1).c"));
        assertEquals("(c (b a 1) 2 3)", parse("a.b(1).c 2, 3"));
        assertEquals("(c (b a 1) (d 2) (d self 3 4))", parse("a.b(1).c 2.d, d(3, 4)"));
    }

    @Test
    void testArgumentAssociativity() throws Exception {
        assertEquals("(one self (two self a b))", parse("one two a, b"));
        assertEquals("(one self (two self a) b)", parse("one two(a), b"));
        assertEquals("(one self (two self a b))", parse("one(two a, b)"));
        assertEquals("(one self (two self a b) c d)", parse("one two(a, b), c, d"));

        assertEquals("(method self a b c) d", parse("method a, \n\n b,\n c\n d"));
        assertEquals("(method self a b c) d", parse("method(a, \n\n b,\n c\n) d"));
        assertEquals("(method self a b (c self (d self (e self f))))",
                parse("method a, \n\n b,\n c d e f"));
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
        assertEquals("(def method [] { a })", parse("def method\na end"));
        assertEquals("(def method:Int [] { a b })", parse("def method:Int\na \n b end"));
        assertEquals("(def method:Int [] { (= a b) (+ a b) })",
                parse("def method:Int\n a = b\n a + b end"));
    }

    @Test
    void testMethodDefinitionsWithArguments() throws Exception {
        assertEquals("(def method [(argument a) (argument b)] { a b })",
                parse("def method(a, b)\na \n b\n end"));

        assertEquals("(def method:Int [(argument a:Int) (argument b:String)] { a b })",
                parse("def method(a:Int, b:String):Int\na \n b\n end"));
    }

    @Test
    void testThrowsExceptionOnParseError() throws Exception {
        assertThrows(Lexer.LexerException.class, () -> parse("1234 $ 4321"));
        assertThrows(Parser.ParserException.class, () -> parse("if end"));
    }

    @Test
    void testClassDefinitions() throws Exception {
        assertEquals("(class Fib { (def fib [] { a }) (def fib2 [] { b }) })",
                parse("class Fib\n def fib\n a end def fib2\n b end end"));

        assertEquals("(class Fib { (= a (+ b 1)) })",
                parse("class Fib\n a = b + 1 end"));

        assertEquals("(class Fib { (attr_reader self \"value\") })",
                parse("class Fib\n attr_reader \"value\"\n end"));
    }

    @Test
    void testClassInheritanceDefinitions() throws Exception {
        assertEquals("(class Fib:BaseFib { a })",
                parse("class Fib < BaseFib\n a end"));
    }

    @Test
    void testSpecialMethodNames() throws Exception {
        assertEquals("(class a b c)", parse("a.class b, c"));
    }

    @Test
    void testStandaloneBlocks() throws Exception {
        assertEquals("{ a b }", parse("begin a\n b end"));
    }

    @Test
    void testCatches() throws Exception {
        assertEquals("{ a b (catch Error { b }) }",
                parse("begin a\n b catch Error\n b end"));

        assertEquals("{ a (catch A { b }) (catch Error { c }) }",
                parse("begin a catch A\n b\n catch Error\n c end"));

        assertEquals("{ a (catch A { b }) (catch Error { c }) }",
                parse("begin a catch A\n b\n catch\n c end"));

        assertEquals("{ a (catch ex:A { b }) (catch ex2:Error { c }) }",
                parse("begin a catch ex:A\n b\n catch ex2\n c end"));
    }

    @Test
    void testLambdas() throws Exception {
        assertEquals("(lambda [] { a b })",
                parse("do a\n b\n end"));

        assertEquals("(lambda [(argument a) (argument b)] { a b })",
                parse("do |a, b| a\n b\n end"));

        assertEquals("(lambda [] { a b })",
                parse("{ a\n b\n }"));

        assertEquals("(lambda [(argument a) (argument b)] { a b })",
                parse("{ |a, b| a\n b\n }"));

        assertEquals("(call (lambda [] { a }) 42)",
                parse("{ a\n }.call 42"));
    }

    @Test
    void testArrayLiterals() throws Exception {
        assertEquals("(new Array a b c d)", parse("[a, b\n,\n c,\n d]"));
    }

//    @Test
//    void testPipes() throws Exception {
//        assertEquals("(finish self (start self))",
//                parse("start | finish"));
//
//        assertEquals("(finish (start self))",
//                parse("start | .finish"));
//    }

    private String parse(String source) throws Parser.ParserException, IOException, Lexer.LexerException {
        List<ASTNode> nodes = ParserSupport.parseAll(source);

        return nodes.stream()
            .map(ASTInspector::inspect)
            .collect(Collectors.joining(" "));
    }
}
