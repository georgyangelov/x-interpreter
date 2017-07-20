package net.gangelov.x.ast.nodes;

import net.gangelov.x.ast.ASTNode;
import net.gangelov.x.ast.AbstractVisitor;

public class ClassDefinitionNode extends ASTNode {
    public final String name;
    public final BlockNode body;

    public ClassDefinitionNode(String name, BlockNode body) {
        super();

        this.name = name;
        this.body = body;
    }

    @Override
    public <T, C> T visit(AbstractVisitor<T, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
