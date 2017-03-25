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
        assertEquals(
            "(EOF 'EOF')",
            lex("")
        );
    }

    @Test
    void testSimpleTokens() throws IOException, Lexer.LexerException {
        assertEquals(
            "(OpenParen '(') (CloseParen ')') (OpenBracket '[') (CloseBracket ']') (Comma ',') " +
                "(Dot '.') (Colon ':') (Number '12345') (String 'test') (Newline '\n') (Name 'doend') (EOF 'EOF')",
            lex("()[],.:12345\"test\"\ndoend")
        );
    }

    @Test
    void testKeywords() throws IOException, Lexer.LexerException {
        assertEquals(
            "(Do 'do') (End 'end') (If 'if') (Elsif 'elsif') (Else 'else') (While 'while') (Def 'def') (EOF 'EOF')",
            lex("do end if elsif else while def")
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
