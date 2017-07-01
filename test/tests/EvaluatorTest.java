package tests;

import net.gangelov.x.ast.ASTNode;
import net.gangelov.x.debug.ASTInspector;
import net.gangelov.x.evaluator.Evaluator;
import net.gangelov.x.parser.Parser;
import net.gangelov.x.runtime.Value;
import org.junit.jupiter.api.Test;
import tests.support.ParserSupport;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class EvaluatorTest {
    @Test
    void testConstants() throws Exception {
        assertEquals("42", eval("42"));
//        assertEquals("4.2", eval("4.2"));
        assertEquals("-42", eval("-42"));
//        assertEquals("-4.2", eval("-4.2"));
        assertEquals("\"test\"", eval("\"test\""));
    }

    @Test
    void testArithmetic() throws Exception {
        assertEquals("5", eval("2 + 3"));
        assertEquals("3", eval("5 - 2"));
        assertEquals("15", eval("5 * 3"));
    }

    @Test
    void testSimpleStringMethods() throws Exception {
        assertEquals("5", eval("\"Hello\".length"));
        assertEquals("\"Hello world!\"", eval("\"Hello \".concat \"world!\""));
    }

    @Test
    void testNestedExpressions() throws Exception {
        assertEquals("6", eval("\"Hello\".length + 1"));
        assertEquals("13", eval("1 + \"Hello\".length + \" world!\".length"));
        assertEquals("31", eval("1 + 3 * 5 * 4 / (3 - 1)"));
    }

    @Test
    void testNoMethodError() throws Exception {
        assertThrows(Evaluator.RuntimeError.class, () -> {
            eval("1.work");
        });

        assertThrows(Evaluator.RuntimeError.class, () -> {
            eval("\"Hello\" + 1");
        });
    }

    private String eval(String program) throws Exception {
        List<ASTNode> nodes = ParserSupport.parseAll(program);
        List<Value> results = new Evaluator(nodes).evaluate();

        return results.stream()
                .map(Value::inspect)
                .collect(Collectors.joining(" "));
    }
}
