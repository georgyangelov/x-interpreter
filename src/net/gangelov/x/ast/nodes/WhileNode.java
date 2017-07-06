package net.gangelov.x.ast.nodes;

import com.sun.istack.internal.NotNull;
import net.gangelov.x.ast.ASTNode;
import net.gangelov.x.ast.AbstractVisitor;

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
