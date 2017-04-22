package net.gangelov.x.evaluator.transforms;

import net.gangelov.x.ast.*;
import net.gangelov.x.evaluator.EvaluatorContext;
import net.gangelov.x.evaluator.TransformVisitor;

public class EvaluatorTransform extends TransformVisitor {
    @Override
    public ASTNode visit(NumberLiteralNode node, EvaluatorContext context) {
        return node;
    }

    @Override
    public ASTNode visit(StringLiteralNode node, EvaluatorContext context) {
        return node;
    }

    @Override
    public ASTNode visit(NameNode node, EvaluatorContext context) {
        return node;
    }

    @Override
    public ASTNode visit(MethodCallNode node, EvaluatorContext context) {
        return node;
    }

    @Override
    public ASTNode visit(BranchNode node, EvaluatorContext context) {
        return node;
    }

    @Override
    public ASTNode visit(BlockNode node, EvaluatorContext context) {
        return node;
    }

    @Override
    public ASTNode visit(WhileNode node, EvaluatorContext context) {
        return node;
    }
}
