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
}
