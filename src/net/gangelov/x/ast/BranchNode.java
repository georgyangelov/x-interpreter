package net.gangelov.x.ast;

import com.sun.istack.internal.NotNull;

public class BranchNode extends ASTNode {
    @NotNull
    public final ASTNode condition;

    @NotNull
    public final BlockNode true_branch, false_branch;

    public BranchNode(ASTNode condition, BlockNode true_branch) {
        this.condition = condition;
        this.true_branch = true_branch;
        this.false_branch = new BlockNode();
    }

    public BranchNode(ASTNode condition, BlockNode true_branch, BlockNode false_branch) {
        this.condition = condition;
        this.true_branch = true_branch;
        this.false_branch = false_branch;
    }

    @Override
    public <T, C> T visit(AbstractVisitor<T, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
