package net.gangelov.x.ast;

public class StringLiteralNode extends LiteralNode {
    public StringLiteralNode(String str) {
        super(str);
    }

    @Override
    public <T, C> T visit(AbstractVisitor<T, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
