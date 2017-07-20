package net.gangelov.x.ast;

import net.gangelov.x.ast.nodes.*;

public class Visitor extends AbstractVisitor<Void, Void> {
    public Void visit(LiteralNode node, Void context) { return null; }
    public Void visit(NameNode node, Void context) { return null; }

    @Override
    public Void visit(AssignmentNode node, Void context) {
        node.value.visit(this, context);

        return null;
    }

    public Void visit(MethodCallNode node, Void context) {
        for (ASTNode n : node.arguments) {
            n.visit(this, context);
        }

        return null;
    }

    public Void visit(BranchNode node, Void context) {
        node.condition.visit(this, context);
        node.true_branch.visit(this, context);
        node.false_branch.visit(this, context);

        return null;
    }

    public Void visit(BlockNode node, Void context) {
        for (ASTNode n : node.nodes) {
            n.visit(this, context);
        }

        return null;
    }

    public Void visit(WhileNode node, Void context) {
        node.condition.visit(this, context);
        node.body.visit(this, context);

        return null;
    }

    public Void visit(MethodDefinitionNode node, Void context) {
        for (ASTNode n : node.arguments) {
            n.visit(this, context);
        }

        return null;
    }

    @Override
    public Void visit(MethodArgumentNode methodDefinitionNode, Void context) {
        return null;
    }

    @Override
    public Void visit(ClassDefinitionNode node, Void context) {
        for (ASTNode n : node.body.nodes) {
            n.visit(this, context);
        }

        return null;
    }
}
