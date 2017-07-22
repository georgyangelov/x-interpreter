package tests;

import net.gangelov.x.ast.ASTNode;
import net.gangelov.x.debug.ASTInspector;
import net.gangelov.x.evaluator.Evaluator;
import net.gangelov.x.parser.Parser;
import net.gangelov.x.runtime.Value;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import tests.support.ParserSupport;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EvaluatorTest {
    @Test
    void testConstants() throws Exception {
        assertEquals("42", eval("42"));
        assertEquals("4.2", eval("4.2"));
        assertEquals("-42", eval("-42"));
        assertEquals("-4.2", eval("-4.2"));
        assertEquals("\"test\"", eval("\"test\""));
        assertEquals("true", eval("true"));
        assertEquals("false", eval("false"));
    }

    @Test
    void testArithmetic() throws Exception {
        assertEquals("5", eval("2 + 3"));
        assertEquals("3", eval("5 - 2"));
        assertEquals("15", eval("5 * 3"));

        assertEquals("5.3", eval("4.3 + 1.0"));
        assertEquals("5.3", eval("1.2 + 4.1"));

        assertEquals("5.0", eval("1.to_f + 4.to_f"));
        assertEquals("5.3", eval("1.3 + 4.to_f"));
        assertEquals("5.3", eval("1.2 + 4.1.to_f"));

        assertEquals("1", eval("1.1.to_i"));
        assertEquals("1", eval("1.5.to_i"));
        assertEquals("1", eval("1.9.to_i"));
    }

    @Test
    void testStringToNumberConversions() throws Exception {
        assertEquals("42", eval("\"42\".to_i"));
        assertEquals("42.2", eval("\"42.2\".to_f"));
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
    void testStringMethods() throws Exception {
        assertEquals("5", eval("\"Hello\".length"));
        assertEquals("\"Hello world!\"", eval("\"Hello \".concat \"world!\""));
        assertEquals("\"Hello world!\"", eval("\"Hello \".concat \"world\", \"!\""));
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
        assertEquals("2", eval("if true \n 2 end"));
        assertEquals("42", eval("if false \n 1 else 42 end"));
        assertEquals("nil", eval("if false \n 1 end"));
        assertEquals("2", eval("if false \n 1 elsif true \n 2 end"));
        assertEquals("3", eval("if false \n 1 elsif false \n 2 else 3 end"));
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
        assertEquals("42", eval("def call(a)\n if a\n 1 else 42 end end call false"));
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
        assertEquals("Global", eval("Object.superclass"));
        assertEquals("nil", eval("Global.superclass"));

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
            eval("begin raise Error.new\n catch UnknownErrorType end");
        });

        assertEquals("nil", eval("begin catch\n end"));
        assertEquals("42", eval("begin 42 catch Error\n catch e\n catch e:Error\n catch\n end"));
        assertEquals("42", eval("begin 42 catch\n end"));

        assertEquals("42", eval("begin raise Error.new\n catch\n 42 end"));
        assertEquals("\"err\"", eval("begin raise Error.new(\"err\")\n catch ex\n ex.message end"));

        assertEquals("\"Silo jammed\"", eval(
                "class LaunchError < Error end\n" +

                "def nuke_city(city)\n" +
                "  raise LaunchError.new(\"Silo jammed\")\n" +
                "end\n" +

                "begin\n" +
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

                "begin\n" +
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

                "begin\n" +
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

                    "begin\n" +
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

    @Test
    void testGlobalMethodsInInstanceContext() throws Exception {
        assertEquals("42", eval(
                "class A\n" +
                "  def crash\n" +
                "    raise Error.new\n" +
                "  end\n" +
                "end\n" +

                "begin\n" +
                "  A.new.crash\n" +
                "catch\n" +
                "  42\n" +
                "end"
        ));
    }

    @Test
    void testSuperCall() throws Exception {
        assertEquals("42", eval(
                "class A\n" +
                "  def test\n" +
                "    41\n" +
                "  end\n" +
                "end\n" +

                "class B < A\n" +
                "  def test\n" +
                "    super + 1\n" +
                "  end\n" +
                "end\n" +

                "B.new.test"
        ));

        assertEquals("42", eval(
                "class A\n" +
                "  def test(num)\n" +
                "    num + 1\n" +
                "  end\n" +
                "end\n" +

                "class B < A\n" +
                "  def test\n" +
                "    super(40) + 1\n" +
                "  end\n" +
                "end\n" +

                "B.new.test"
        ));

        assertError("Cannot call super outside of a method", () -> {
            eval("super + 1");
        });

        assertError("No method test on class Object", () -> {
            eval("class A\n def test\n super end end\n A.new.test");
        });
    }

    @Test
    void testLambdas() throws Exception {
        assertEquals("42", eval("do 42 end.call"));
        assertEquals("42", eval("do |a| a + 41 end.call(1)"));
        assertEquals("42", eval("add_one = do |a| a + 1 end\n add_one.call(41)"));
        assertEquals("42", eval("add = do |a, b| a + b end\n add.call(40, 2)"));

        assertEquals("42", eval("{ 42 }.call"));
        assertEquals("42", eval("{ |a| a + 41 }.call(1)"));
        assertEquals("42", eval("add_one = { |a| a + 1 }\n add_one.call(41)"));
        assertEquals("42", eval("add = { |a, b| a + b }\n add.call(40, 2)"));

        assertEquals("\"Ivan\"", eval(
                "class A\n" +
                "  def initialize\n" +
                "    @name = \"Ivan\"\n" +
                "  end\n" +

                "  def name_lambda\n" +
                "    { @name }\n" +
                "  end\n" +
                "end\n" +

                "A.new.name_lambda.call"
        ));

        assertEquals("42", eval(
                "x = 666\n" +
                "get_x = { x }\n" +
                "x = 42\n" +

                "get_x.call"
        ));
    }

    @Test
    void testArgumentCountChecks() throws Exception {
        assertError("Invalid number of arguments for method test. Expected 3, got 4", () -> {
            eval("def test(a, b, c) end\n test 1, 2, 3, 4");
        });

        assertError("Invalid number of arguments for method test. Expected 3, got 2", () -> {
            eval("def test(a, b, c) end\n test 1, 2");
        });

        assertError("Invalid number of arguments for method test. Expected 3, got 0", () -> {
            eval("def test(a, b, c) end\n test");
        });

        assertError("Invalid number of arguments for method test. Expected 0, got 1", () -> {
            eval("def test\n end\n test 1");
        });

        assertError("Invalid number of arguments for method initialize. Expected 0..1, got 2", () -> {
            eval("Error.new 1, 2");
        });

        assertError("Invalid number of arguments for method <lambda>. Expected 1, got 2", () -> {
            eval("{ |a| a + a }.call 1, 2");
        });
    }

    @Test
    void testArrays() throws Exception {
        assertEquals("0", eval("Array.new.size"));
        assertEquals("1", eval(
                "a = Array.new\n" +
                "a.push true\n" +

                "a.size"
        ));

        assertEquals("42", eval(
                "a = Array.new\n" +
                "a.push 1\n" +
                "a.push 2\n" +
                "a.push 42\n" +

                "a.get 2"
        ));

        assertEquals("\"hello\"", eval(
                "a = Array.new\n" +
                "a.push 42\n" +
                "a.push \"hello\"\n" +

                "a.get 1"
        ));

        assertEquals("[1, 2, \"three\"]", eval(
                "a = Array.new\n" +
                "a.push 1\n" +
                "a.push 2\n" +
                "a.push \"three\"\n" +

                "a"
        ));

        assertEquals("[1, [2, 3]]", eval(
                "a = Array.new\n" +
                "a.push 2\n" +
                "a.push 3\n" +

                "b = Array.new\n" +
                "b.push 1\n" +
                "b.push a\n" +

                "b"
        ));

        assertEquals("[1, 2, 3]", eval("Array.new 1, 2, 3"));
    }

    @Test
    void testArrayLiterals() throws Exception {
        assertEquals("[1, 2, 3]", eval("[1, 2, 3]"));
        assertEquals("5", eval("1 + [1, 2, 3, 5].size"));
    }

    @Test
    void testInheritanceFromBuiltInClasses() throws Exception {
        assertError("Cannot inherit from built-in class Array", () -> {
            eval("class My < Array end");
        });

        assertError("Cannot inherit from built-in class AST", () -> {
            eval("class My < AST end");
        });

        assertError("Cannot inherit from built-in class Bool", () -> {
            eval("class My < Bool end");
        });

        assertError("Cannot inherit from built-in class Float", () -> {
            eval("class My < Float end");
        });

        assertError("Cannot inherit from built-in class Int", () -> {
            eval("class My < Int end");
        });

        assertError("Cannot inherit from built-in class Lambda", () -> {
            eval("class My < Lambda end");
        });

        assertError("Cannot inherit from built-in class Nil", () -> {
            eval("class My < Nil end");
        });

        assertError("Cannot inherit from built-in class String", () -> {
            eval("class My < String end");
        });
    }

    @Test
    void testCannotUseNewOnBuiltinClasses() throws Exception {
        assertError("Cannot instantiate built-in class AST", () -> {
            eval("AST.new");
        });

        assertError("Cannot instantiate built-in class Bool", () -> {
            eval("Bool.new");
        });

        assertError("Cannot instantiate built-in class Float", () -> {
            eval("Float.new");
        });

        assertError("Cannot instantiate built-in class Int", () -> {
            eval("Int.new");
        });

        assertError("Cannot instantiate built-in class Lambda", () -> {
            eval("Lambda.new");
        });

        assertError("Cannot instantiate built-in class Nil", () -> {
            eval("Nil.new");
        });

        assertError("Cannot instantiate built-in class String", () -> {
            eval("String.new");
        });
    }

    @Test
    void testBindLambda() throws Exception {
        assertEquals("42", eval(
                "class A\n" +
                "  def answer(a)\n" +
                "    a + 1\n" +
                "  end\n" +
                "end\n" +

                "{ |x| answer(x) }.bind(A.new).call(41)"
        ));
    }

    @Test
    void testStaticMethods() throws Exception {
        assertEquals("42", eval(
                "class A\n" +
                "  static do\n" +
                "    def answer\n 42 end\n" +
                "  end\n" +
                "end\n" +

                "A.answer"
        ));

        assertEquals("42", eval(
                "class A\n" +
                "  static do\n" +
                "    def answer\n 42 end\n" +
                "  end\n" +
                "end\n" +

                "class B < A end\n" +

                "B.answer"
        ));

        assertEquals("42", eval(
                "class A\n" +
                "  static do\n" +
                "    def answer\n 42 end\n" +
                "  end\n" +
                "end\n" +

                "class B < A\n" +
                "  answer\n" +
                "end"
        ));
    }

    private String eval(String program) throws Exception {
        List<ASTNode> nodes = ParserSupport.parseAll(program);
        List<Value> results = new Evaluator().evaluate(nodes);

        return results.stream()
                .reduce((first, second) -> second)
                .map(Value::inspect)
                .get();
    }

    private void assertError(String message, Executable executable) {
        Throwable error = assertThrows(Evaluator.RuntimeError.class, executable);

        assertEquals(message, error.getMessage());
    }
}
