package net.gangelov.x.ast;

public class NameNode extends ASTNode {
    public final String name;

    public NameNode(String name) {
        super();
        this.name = name;
    }

    @Override
    public <T, C> T visit(AbstractVisitor<T, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
