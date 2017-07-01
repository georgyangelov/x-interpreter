package net.gangelov.x.evaluator;

import net.gangelov.x.ast.ASTNode;
import net.gangelov.x.runtime.Value;

import java.util.List;
import java.util.stream.Collectors;

public class Evaluator {
    private List<ASTNode> nodes;
    private EvaluatorTransform transformer = new EvaluatorTransform();
    private EvaluatorContext context = new EvaluatorContext();

    public Evaluator(List<ASTNode> nodes) {
        this.nodes = nodes;
    }

    public List<Value> evaluate() {
        return nodes.stream()
                .map(node -> node.visit(transformer, context))
                .collect(Collectors.toList());
    }
}
