package tests;

import net.gangelov.x.parser.Lexer;
import net.gangelov.x.debug.LexerInspector;
import net.gangelov.x.parser.Token;
import net.gangelov.x.parser.TokenType;
import org.junit.jupiter.api.*;
import tests.support.ParserSupport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {
    @Test
    void testEOF() throws IOException, Lexer.LexerException {
        assertEquals("(EOF 'EOF')", lex(""));
    }

    @Test
    void testInlineComments() throws Exception {
        assertEquals("(EOF 'EOF')", lex("# test"));
        assertEquals("(EOF 'EOF')", lex("# # # # # test"));
        assertEquals("(Number '1234') (EOF 'EOF')", lex("# test \n 1234"));
    }

    @Test
    void testSimpleTokens() throws IOException, Lexer.LexerException {
        assertEquals(
            "(OpenParen '(') (CloseParen ')') (OpenBracket '[') (CloseBracket ']') (Comma ',') " +
            "(Dot '.') (Colon ':') (Number '12345') (String 'test') (Newline '\n') " +
            "(Name 'doend') (EOF 'EOF')",
            lex("()[],.:12345\"test\"\ndoend")
        );
    }

    @Test
    void testNumbers() throws IOException, Lexer.LexerException {
        assertEquals("(Number '1234') (EOF 'EOF')", lex("1234"));
        assertEquals("(Number '1234.5678') (EOF 'EOF')", lex("1234.5678"));
    }

    @Test
    void testLiterals() throws IOException, Lexer.LexerException {
        assertEquals("(Nil 'nil') (EOF 'EOF')", lex("nil"));
        assertEquals("(Bool 'true') (EOF 'EOF')", lex("true"));
        assertEquals("(Bool 'false') (EOF 'EOF')", lex("false"));
    }

    @Test
    void testCallsOnLiterals() throws IOException, Lexer.LexerException {
        assertEquals("(Number '42.2') (Dot '.') (Name 'abs') (EOF 'EOF')", lex("42.2.abs"));
        assertEquals("(String 'test') (Dot '.') (Name 'length') (Number '3') (EOF 'EOF')",
                lex("\"test\".length 3"));
    }

    @Test
    void testKeywords() throws IOException, Lexer.LexerException {
        assertEquals(
            "(Do 'do') (End 'end') (If 'if') (Elsif 'elsif') (Else 'else') " +
            "(While 'while') (Def 'def') (Class 'class') (EOF 'EOF')",
            lex("do end if elsif else while def class")
        );
    }

    @Test
    void testEscapeSequences() throws IOException, Lexer.LexerException {
        assertEquals(
            "(String 'a\nb\\c\td') (EOF 'EOF')",
            lex("\"a\\nb\\\\c\\td\"")
        );
    }

    @Test
    void testUnicode() throws IOException, Lexer.LexerException {
        assertEquals(
            "(Name 'тест') (String 'тест') (EOF 'EOF')",
            lex("тест \"тест\"")
        );
    }

    @Test
    void testAllowsUnderscoreInNames() throws IOException, Lexer.LexerException {
        assertEquals(
            "(Name '_this_is_aValidName') (EOF 'EOF')",
            lex(" _this_is_aValidName ")
        );
    }

    @Test
    void testSpecialSymbolsInNames() throws IOException, Lexer.LexerException {
        assertEquals(
            "(Name '@an_instance_variable') (EOF 'EOF')",
            lex("@an_instance_variable")
        );

        assertEquals(
            "(Name 'valid_name?') (Name 'valid_name!') (EOF 'EOF')",
            lex("valid_name? valid_name!")
        );

        assertEquals(
            "(Name 'invalid_nam!') (Name 'e') (Name 'invalid_n?') (Name 'ame') (EOF 'EOF')",
            lex("invalid_nam!e invalid_n?ame")
        );
    }

    @Test
    void testExceptionOnUnknownToken() {
        assertThrows(Lexer.LexerException.class, () -> {
           lex("test %");
        });
    }

    @Test
    void testBraces() throws IOException, Lexer.LexerException {
        assertEquals(
            "(OpenBrace '{') (CloseBrace '}') (EOF 'EOF')",
            lex("{}")
        );
    }

    @Test
    void testUnaryOperators() throws IOException, Lexer.LexerException {
        assertEquals("(UnaryOperator '!') (Name 'a') (EOF 'EOF')", lex("!a"));
        assertEquals("(UnaryOperator '!') (UnaryOperator '!') (Name 'a') (EOF 'EOF')", lex("!!a"));
    }

    @Test
    void testBinaryOperators() throws IOException, Lexer.LexerException {
        assertEquals("(Name 'a') (BinaryOperator '==') (Name 'b') (EOF 'EOF')", lex("a== b"));
        assertEquals("(BinaryOperator '+') (BinaryOperator '-') (EOF 'EOF')", lex("+ -"));
        assertEquals("(BinaryOperator 'and') (BinaryOperator 'or') (EOF 'EOF')", lex("and or"));
        assertEquals("(BinaryOperator '*') (BinaryOperator '/') (EOF 'EOF')", lex("* /"));
        assertEquals("(BinaryOperator '<') (BinaryOperator '>') (EOF 'EOF')", lex("< >"));
        assertEquals("(BinaryOperator '<=') (BinaryOperator '>=') (BinaryOperator '!=') (EOF 'EOF')",
                lex("<= >= !="));
        assertEquals("(BinaryOperator '+=') (BinaryOperator '-=') (EOF 'EOF')", lex("+= -="));
        assertEquals("(BinaryOperator '/=') (BinaryOperator '*=') (EOF 'EOF')", lex("/= *="));
        assertEquals("(Name 'a') (BinaryOperator '=') (Name 'b') (EOF 'EOF')", lex("a = b"));
    }

    private String lex(String str) throws IOException, Lexer.LexerException {
        List<Token> tokens = new ArrayList<>();
        Lexer lexer = ParserSupport.lexerForString(str);
        Token t = lexer.nextToken();

        while (t.type != TokenType.EOF) {
            tokens.add(t);
            t = lexer.nextToken();
        }

        tokens.add(t);

        return LexerInspector.inspectTokens(tokens);
    }
}
