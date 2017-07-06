package net.gangelov.x.ast.nodes;

import net.gangelov.x.ast.ASTNode;
import net.gangelov.x.ast.AbstractVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MethodCallNode extends ASTNode {
    public final String name;
    public final List<ASTNode> arguments;

    public MethodCallNode(String name, List<ASTNode> arguments) {
        super();
        this.name = name;
        this.arguments = arguments;
    }

    public MethodCallNode(String name, ASTNode... nodes) {
        super();
        this.name = name;
        this.arguments = Arrays.asList(nodes);
    }

    public MethodCallNode(String name, ASTNode target, List<ASTNode> arguments) {
        super();
        this.name = name;
        this.arguments = new ArrayList<>();
        this.arguments.add(target);
        this.arguments.addAll(arguments);
    }

    @Override
    public <T, C> T visit(AbstractVisitor<T, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
