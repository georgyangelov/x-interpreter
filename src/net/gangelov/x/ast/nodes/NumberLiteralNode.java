package net.gangelov.x.ast.nodes;

import net.gangelov.x.ast.AbstractVisitor;

public class NumberLiteralNode extends LiteralNode {
    public NumberLiteralNode(String str) {
        super(str);
    }

    @Override
    public <T, C> T visit(AbstractVisitor<T, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
