package net.gangelov.x.ast;

public class Visitor {
    public void visit(NumberLiteralNode node) {}
    public void visit(StringLiteralNode node) {}
    public void visit(NameNode node) {}

    public void visit(MethodCallNode node) {
        for (ASTNode n : node.arguments) {
            n.visit(this);
        }
    }

    public void visit(BranchNode node) {
        node.condition.visit(this);
        node.true_branch.visit(this);
        node.false_branch.visit(this);
    }

    public void visit(BlockNode node) {
        for (ASTNode n : node.nodes) {
            n.visit(this);
        }
    }
}
