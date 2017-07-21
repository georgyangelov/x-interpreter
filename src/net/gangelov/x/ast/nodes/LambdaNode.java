package net.gangelov.x.ast.nodes;

import net.gangelov.x.ast.ASTNode;
import net.gangelov.x.ast.AbstractVisitor;

import java.util.List;

public class LambdaNode extends ASTNode {
    public final List<MethodArgumentNode> arguments;
    public final BlockNode body;

    public LambdaNode(List<MethodArgumentNode> arguments, BlockNode body) {
        this.arguments = arguments;
        this.body = body;
    }

    @Override
    public <T, C> T visit(AbstractVisitor<T, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
