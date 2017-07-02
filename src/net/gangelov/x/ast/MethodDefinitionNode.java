package net.gangelov.x.ast;

import java.util.List;

public class MethodDefinitionNode extends ASTNode {
    public final String name, returnType;
    public final List<MethodArgumentNode> arguments;
    public final BlockNode body;

    public MethodDefinitionNode(
            String name,
            String returnType,
            List<MethodArgumentNode> arguments,
            BlockNode body
    ) {
        this.name = name;
        this.returnType = returnType;
        this.arguments = arguments;
        this.body = body;
    }

    @Override
    public <T, C> T visit(AbstractVisitor<T, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
