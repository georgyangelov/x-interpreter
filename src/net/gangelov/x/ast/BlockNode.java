package net.gangelov.x.ast;

import java.util.ArrayList;
import java.util.List;

public class BlockNode extends ASTNode {
    public final List<ASTNode> nodes;

    public BlockNode() {
        this.nodes = new ArrayList<>();
    }

    public BlockNode(List<ASTNode> nodes) {
        this.nodes = nodes;
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    @Override
    public void visit(Visitor visitor) {
        visitor.visit(this);
    }
}
