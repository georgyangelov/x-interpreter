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
        assertEquals("Class", eval("String.class"));
        assertEquals("Class", eval("String.class.class"));
        assertEquals("Class", eval("Class.class"));
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

        assertThrows(Evaluator.RuntimeError.class, () -> {
            eval("42.new");
        });

        assertThrows(Evaluator.RuntimeError.class, () -> {
            eval("class Answer\n def give\n 42 end end\n Answer.new.new");
        });
    }

    @Test
    void testInstanceVariables() throws Exception {
        assertEquals("42", eval(
                "class Answer\n" +
                "  def set(a)\n" +
                "    @answer = a\n" +
                "  end\n" +

                "  def get\n" +
                "    @answer\n" +
                "  end\n" +
                "end\n" +

                "a = Answer.new\n" +
                "a.set 42\n" +
                "a.get"
        ));

        assertThrows(Evaluator.RuntimeError.class, () -> {
            eval("class Answer\n def give\n @answer end end\n Answer.new.give");
        });
    }

    @Test
    void testInitializers() throws Exception {
        assertEquals("42", eval(
                "class Answer\n" +
                "  def initialize(a, b)\n" +
                "    @answer = a + b\n" +
                "  end\n" +

                "  def get\n" +
                "    @answer\n" +
                "  end\n" +
                "end\n" +

                "a = Answer.new 41, 1\n" +
                "a.get"
        ));
    }

    @Test
    void testClassRedefinition() throws Exception {
        assertThrows(Evaluator.RuntimeError.class, () -> {
           eval("class Test end\n class Test end");
        });
    }

    @Test
    void testClassInheritance() throws Exception {
        assertEquals("Object", eval("class A end\n A.superclass"));
        assertEquals("nil", eval("Object.superclass"));

        assertEquals("A", eval(
                "class A\n" +
                "  def answer\n" +
                "    42\n" +
                "  end\n" +
                "end\n" +

                "class B < A end\n" +

                "B.superclass"
        ));

        assertEquals("42", eval(
                "class A\n" +
                "  def answer\n" +
                "    42\n" +
                "  end\n" +
                "end\n" +

                "class B < A end\n" +

                "B.new.answer"
        ));

        assertEquals("666", eval(
                "class A\n" +
                "  def answer\n" +
                "    42\n" +
                "  end\n" +
                "end\n" +

                "class B < A\n" +
                "  def answer\n" +
                "    666\n" +
                "  end\n" +
                "end\n" +

                "B.new.answer"
        ));

        assertThrows(Evaluator.RuntimeError.class, () -> {
            eval("class Test < A end");
        });
    }

    @Test
    void testErrors() throws Exception {
        assertThrows(Evaluator.RuntimeError.class, () -> {
            eval("raise Error.new(\"Unhandled error\")");
        });

        assertThrows(Evaluator.RuntimeError.class, () -> {
            eval("do raise Error.new\n catch UnknownErrorType end");
        });

        assertEquals("nil", eval("do catch\n end"));
        assertEquals("42", eval("do 42 catch Error\n catch e\n catch e:Error\n catch\n end"));
        assertEquals("42", eval("do 42 catch\n end"));

        assertEquals("42", eval("do raise Error.new\n catch\n 42 end"));
        assertEquals("\"err\"", eval("do raise Error.new(\"err\")\n catch ex\n ex.message end"));

        assertEquals("\"Silo jammed\"", eval(
                "class LaunchError < Error end\n" +

                "def nuke_city(city)\n" +
                "  raise LaunchError.new(\"Silo jammed\")\n" +
                "end\n" +

                "do\n" +
                "  nuke_city \"Sofia\"\n" +
                "  this_is_not_a_valid_method\n" +
                "catch error\n" +
                "  error.message\n" +
                "catch error:LaunchError\n" +
                "  42\n" +
                "end"
        ));

        assertEquals("\"Silo jammed\"", eval(
                "class LaunchError < Error end\n" +
                "class AbortError < Error end\n" +

                "def nuke_city(city)\n" +
                "  raise LaunchError.new(\"Silo jammed\")\n" +
                "end\n" +

                "do\n" +
                "  nuke_city \"Sofia\"\n" +
                "  this_is_not_a_valid_method\n" +
                "catch AbortError\n" +
                "  42\n" +
                "catch error\n" +
                "  error.message\n" +
                "end"
        ));

        assertEquals("\"Silo jammed\"", eval(
                "class LaunchError < Error end\n" +
                "class AbortError < Error end\n" +

                "def nuke_city(city)\n" +
                "  raise LaunchError.new(\"Silo jammed\")\n" +
                "end\n" +

                "do\n" +
                "  nuke_city \"Sofia\"\n" +
                "  this_is_not_a_valid_method\n" +
                "catch AbortError\n" +
                "  42\n" +
                "catch error:LaunchError\n" +
                "  error.message\n" +
                "end"
        ));



        assertThrows(Evaluator.RuntimeError.class, () -> {
            eval(
                    "class LaunchError < Error end\n" +
                    "class AbortError < Error end\n" +

                    "def nuke_city(city)\n" +
                    "  raise LaunchError.new(\"Silo jammed\")\n" +
                    "end\n" +

                    "do\n" +
                    "  nuke_city \"Sofia\"\n" +
                    "  this_is_not_a_valid_method\n" +
                    "catch AbortError\n" +
                    "  42\n" +
                    "end"
            );
        });
    }

    @Test
    void testCatchInImplicitBlocks() throws Exception {
        assertEquals("42", eval(
                "if 1 == 1\n" +
                "  raise Error.new\n" +
                "catch\n" +
                "  42\n" +
                "end"
        ));

        assertEquals("false", eval(
                "i = true\n" +

                "while i\n" +
                "  raise Error.new\n" +
                "catch\n" +
                "  i = false\n" +
                "end"
        ));

        assertEquals("42", eval(
                "def test\n" +
                "  raise Error.new\n" +
                "catch\n" +
                "  42\n" +
                "end\n" +

                "test"
        ));
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
