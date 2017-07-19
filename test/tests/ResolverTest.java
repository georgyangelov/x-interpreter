package tests;

import net.gangelov.x.ast.ASTNode;
import net.gangelov.x.resolver.Resolver;
import net.gangelov.x.resolver.ResolverBuilder;
import net.gangelov.x.resolver.ResolverScope;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import tests.support.ParserSupport;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResolverTest {
//    @Test
//    void testConstantTypeResolve() throws Exception {
//        assertEquals("Int", resolve("42"));
//        assertEquals("String", resolve("\"hello\""));
//    }

    @Test
    void testSimpleNameResolve() throws Exception {
        assertEquals("Int", resolve("a = 5", "a"));
        assertEquals("String", resolve("a = \"test\"", "a"));
    }

    @Test
    void testTypePropagation() throws Exception {
        assertEquals("Int", resolve("a = 1\n b = a\n c = b", "c"));
    }

    @Test
    void testCyclicPropagation() throws Exception {
        assertEquals("Int", resolve("a = 1\n a = a", "a"));
    }

    @Test
    void testConflictingTypes() throws Exception {
        assertTypeError("Type String cannot be assigned to Int", () -> {
            resolve("a = 1\n a = \"test\"", "a");
        });

        assertTypeError("Type Int cannot be assigned to String", () -> {
            resolve("a = \"test\"\n a = 42", "a");
        });
    }

    @Test
    void testMethodExistenceCheck() throws Exception {
        assertTypeError("Method bla does not exist on type Int", () -> {
            resolve("a = 123.bla", "a");
        });
    }

    @Test
    void testSimpleMethodResolve() throws Exception {
        assertEquals("Int", resolve("a = \"test\".length", "a"));
    }

//    @Test
//    void testMethodResolve() throws Exception {
//        assertEquals("Int", resolve("a = 1 + 2\n a"));
//        assertEquals("Int", resolve("a = 1\n b = 2\n c = a + b\n c"));
//        assertEquals("String", resolve("a = \"test\"\n b = a.concat(\"1\")\n b"));
//        assertEquals("Int", resolve("a = \"test\".length\n a"));
//    }

//    @Test
//    void testUnknownNames() throws Exception {
//        assertTypeError("The type of a cannot be inferred", () -> {
//            resolve("a");
//        });
//
//        assertTypeError("The type of a cannot be inferred", () -> {
//            resolve("b = 1 + a\n 42");
//        });
//
//        assertTypeError("The type of a cannot be inferred", () -> {
//            resolve("b = 1 + 2\n a");
//        });
//    }

    private String resolve(String code, String variable) throws Exception {
        List<ASTNode> nodes = ParserSupport.parseAll(code);
        Resolver resolver = new Resolver();
        ResolverScope scope = new ResolverScope();
        ResolverBuilder builder = new ResolverBuilder(resolver, scope);

        for (ASTNode node : nodes) {
            builder.build(node);
        }

        resolver.resolve();

        return scope.getVariable(variable).type.name;
    }

    private void assertTypeError(String message, Executable executable) {
        Throwable error = assertThrows(Resolver.ResolveError.class, executable);

        assertEquals(message, error.getMessage());
    }
}
