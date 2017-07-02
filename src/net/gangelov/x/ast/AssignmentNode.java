package net.gangelov.x.ast;

public class AssignmentNode extends ASTNode {
    public final String name;
    public final ASTNode value;

    public AssignmentNode(String name, ASTNode value) {
        super();

        this.name = name;
        this.value = value;
    }

    @Override
    public <T, C> T visit(AbstractVisitor<T, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
