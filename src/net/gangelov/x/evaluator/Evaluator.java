package net.gangelov.x.evaluator;

import net.gangelov.x.ast.ASTNode;
import net.gangelov.x.runtime.Runtime;
import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.builtins.GlobalValue;
import net.gangelov.x.runtime.classes.*;

import java.util.List;
import java.util.stream.Collectors;

public class Evaluator {
    // TODO: Use non-runtime exception?
    public static class RuntimeError extends RuntimeException {
        public RuntimeError(String message) {
            super(message);
        }
    }

    private Runtime runtime = new Runtime();
    private EvaluatorTransform transformer = new EvaluatorTransform(runtime);
    private EvaluatorContext context = new EvaluatorContext();

    public Evaluator() {
        defineBuiltins();
    }

    private void defineBuiltins() {
        context.defineLocal("Self", runtime.GlobalClass);
        context.defineLocal("self", runtime.GLOBAL);
        context.defineLocal("global", runtime.GLOBAL);
    }

    public List<Value> evaluate(List<ASTNode> nodes) {
        return nodes.stream()
                .map(node -> node.visit(transformer, context))
                .collect(Collectors.toList());
    }
}
