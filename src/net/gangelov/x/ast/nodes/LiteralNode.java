package net.gangelov.x.ast.nodes;

import net.gangelov.x.ast.ASTNode;
import net.gangelov.x.ast.AbstractVisitor;

public class LiteralNode extends ASTNode {
    public enum Type { Int, String, Bool, Nil }

    public final Type type;
    public String str;

    public LiteralNode(Type type, String str) {
        this.type = type;
        this.str = str;
    }

    @Override
    public <T, C> T visit(AbstractVisitor<T, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
