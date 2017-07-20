package tests;

import net.gangelov.x.ast.ASTNode;
import net.gangelov.x.typeresolver.TypeGraphBuilder;
import net.gangelov.x.typeresolver.Resolver;
import net.gangelov.x.typeresolver.TypeError;
import net.gangelov.x.types.TypeEnvironment;
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
//
//    @Test
//    void testSimpleNameResolve() throws Exception {
//        assertEquals("Int", resolve("a = 5\n a"));
//        assertEquals("String", resolve("a = \"test\"\n a"));
//    }
//
//    @Test
//    void testTypePropagation() throws Exception {
//        assertEquals("Int", resolve("a = 1\n b = a\n c = b\n c"));
//    }
//
//    @Test
//    void testCyclicPropagation() throws Exception {
//        assertEquals("Int", resolve("a = 1\n a = a\n a"));
//    }
//
//    @Test
//    void testConflictingTypes() throws Exception {
//        assertTypeError("Type String cannot be assigned to Int", () -> {
//            resolve("a = 1\n a = \"test\"");
//        });
//
//        assertTypeError("Type Int cannot be assigned to String", () -> {
//            resolve("a = \"test\"\n a = 42");
//        });
//    }

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

    private String resolve(String code) throws Exception {
        List<ASTNode> nodes = ParserSupport.parseAll(code);

        TypeEnvironment types = new TypeEnvironment();
        TypeGraphBuilder graphBuilder = new TypeGraphBuilder(types);

        nodes.forEach(node -> node.visit(graphBuilder, null));

        Resolver resolver = new Resolver(graphBuilder, types);

        resolver.resolve();

        return resolver.typeOf(nodes.get(nodes.size() - 1)).name;
    }

    private void assertTypeError(String message, Executable executable) {
        Throwable error = assertThrows(TypeError.class, executable);

        assertEquals(message, error.getMessage());
    }
}
