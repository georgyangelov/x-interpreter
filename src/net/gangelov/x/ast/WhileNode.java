package net.gangelov.x.ast;

import com.sun.istack.internal.NotNull;

public class WhileNode extends ASTNode {
    @NotNull
    public final ASTNode condition;

    @NotNull
    public final BlockNode body;

    public WhileNode(ASTNode condition, BlockNode body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public <T, C> T visit(AbstractVisitor<T, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
