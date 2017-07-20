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
        assertEquals("true", eval("true"));
        assertEquals("false", eval("false"));
    }

    @Test
    void testArithmetic() throws Exception {
        assertEquals("5", eval("2 + 3"));
        assertEquals("3", eval("5 - 2"));
        assertEquals("15", eval("5 * 3"));
    }

    @Test
    void testBooleans() throws Exception {
        assertEquals("true", eval("!false"));
        assertEquals("false", eval("!true"));

        assertEquals("true", eval("true == true"));
        assertEquals("true", eval("false == false"));
        assertEquals("false", eval("true == false"));

        assertEquals("false", eval("true != true"));
        assertEquals("false", eval("false != false"));
        assertEquals("true", eval("true != false"));
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

    @Test
    void testIf() throws Exception {
        assertEquals("2", eval("if 1 \n 2 end"));
        assertEquals("42", eval("if 0 \n 1 else 42 end"));
        assertEquals("nil", eval("if 0 \n 1 end"));
        assertEquals("2", eval("if 0 \n 1 elsif 1 \n 2 end"));
        assertEquals("3", eval("if 0 \n 1 elsif 0 \n 2 else 3 end"));
    }

    @Test
    void testAssignment() throws Exception {
        assertEquals("5", eval("a = 5"));
        assertEquals("6", eval("a = 5 \n a + 1"));
        assertEquals("7", eval("a = 5 \n a = a + 1 \n a + 1"));
    }

    @Test
    void testGlobalMethods() throws Exception {
        assertEquals("42", eval("global.the_answer"));
        assertEquals("42", eval("self.the_answer"));
        assertEquals("42", eval("the_answer"));

        assertThrows(Evaluator.RuntimeError.class, () -> {
            eval("hey");
        });
    }

    @Test
    void testSimpleMethodDefinitions() throws Exception {
        assertEquals("\"method_name\"", eval("def method_name 1 end"));
        assertEquals("\"hello\"", eval("def method_name \"hello\" end method_name"));
        assertEquals("42", eval("def give_answer 42 end self.give_answer"));
    }

    @Test
    void testMethodDefinitionsWithArguments() throws Exception {
        assertEquals("42", eval("def call(a) a end call(42)"));
        assertEquals("42", eval("def call(a)\n if a\n 1 else 42 end end call 0"));
    }

    @Test
    void testRecursion() throws Exception {
        assertEquals("120", eval(
                "def factorial(n)\n" +
                "  if n == 1\n" +
                "    1\n" +
                "  else\n" +
                "    n * factorial(n - 1)\n" +
                "  end\n" +
                "end\n" +

                "factorial(5)"
        ));
    }

    @Test
    void testWhile() throws Exception {
        assertEquals("120", eval(
                "def factorial(n)\n" +
                "  i = n\n" +
                "  result = 1" +

                "  while i > 0\n" +
                "    result = result * i\n" +
                "    i = i - 1\n" +
                "  end\n" +

                "  result\n" +
                "end\n" +

                "factorial(5)"
        ));
    }

    @Test
    void testClassesAsObjects() throws Exception {
        assertEquals("\"Hello world!\"", eval("String.hello"));
    }

    @Test
    void testClassDefinitions() throws Exception {
        assertEquals("42", eval("class Test 42 end"));
        assertEquals("Test", eval("class Test end Test"));
        assertEquals("Test", eval("class Test\n def bla\n test end end Test"));
    }

    @Test
    void testClassInstantiation() throws Exception {
        assertEquals("42", eval("class Answer\n def give\n 42 end end\n Answer.new.give"));

        assertThrows(Evaluator.RuntimeError.class, () -> {
            eval("class Answer\n def give\n 42 end end\n Answer.give");
        });
    }

    @Test
    void testClassRedefinition() throws Exception {
        assertThrows(Evaluator.RuntimeError.class, () -> {
           eval("class Test end\n class Test end");
        });
    }

    private String eval(String program) throws Exception {
        List<ASTNode> nodes = ParserSupport.parseAll(program);
        List<Value> results = new Evaluator(nodes).evaluate();

        return results.stream()
                .reduce((first, second) -> second)
                .map(Value::inspect)
                .get();
    }
}
