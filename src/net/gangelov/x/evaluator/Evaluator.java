package net.gangelov.x.evaluator;

import net.gangelov.x.ast.ASTNode;
import net.gangelov.x.evaluator.transforms.EvaluatorTransform;

import java.util.List;
import java.util.stream.Collectors;

public class Evaluator {
    private List<ASTNode> nodes;
    private EvaluatorTransform transformVisitor = new EvaluatorTransform();
    private EvaluatorContext context = new EvaluatorContext();

    public Evaluator(List<ASTNode> nodes) {
        this.nodes = nodes;
    }

    public List<ASTNode> evaluate() {
        return nodes.stream()
                .map(node -> node.visit(transformVisitor, context))
                .collect(Collectors.toList());
    }
}
