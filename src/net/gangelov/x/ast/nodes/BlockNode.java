package net.gangelov.x.ast.nodes;

import net.gangelov.x.ast.ASTNode;
import net.gangelov.x.ast.AbstractVisitor;

import java.util.ArrayList;
import java.util.List;

public class BlockNode extends ASTNode {
    public final List<ASTNode> nodes;
    public final List<CatchNode> catchNodes;

    public BlockNode() {
        this.nodes = new ArrayList<>();
        this.catchNodes = new ArrayList<>();
    }

    public BlockNode(List<ASTNode> nodes) {
        this.nodes = nodes;
        this.catchNodes = new ArrayList<>();
    }

    public BlockNode(List<ASTNode> nodes, List<CatchNode> catchNodes) {
        this.nodes = nodes;
        this.catchNodes = catchNodes;
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    @Override
    public <T, C> T visit(AbstractVisitor<T, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
