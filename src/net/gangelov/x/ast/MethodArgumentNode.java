package net.gangelov.x.ast;

public class MethodArgumentNode extends ASTNode {
    public final String name, type;

    public MethodArgumentNode(String name, String type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public <T, C> T visit(AbstractVisitor<T, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
