package net.gangelov.x.ast.nodes;

import net.gangelov.x.ast.ASTNode;
import net.gangelov.x.ast.AbstractVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MethodCallNode extends ASTNode {
    public final String name;
    public final List<ASTNode> arguments;
    public final boolean mayBeVariableCall;

    public MethodCallNode(String name, boolean mayBeVariableCall, List<ASTNode> arguments) {
        super();
        this.name = name;
        this.arguments = arguments;
        this.mayBeVariableCall = mayBeVariableCall;
    }

    public MethodCallNode(String name, boolean mayBeVariableCall, ASTNode... nodes) {
        super();
        this.name = name;
        this.arguments = Arrays.asList(nodes);
        this.mayBeVariableCall = mayBeVariableCall;
    }

    public MethodCallNode(String name, boolean mayBeVariableCall, ASTNode target, List<ASTNode> arguments) {
        super();
        this.name = name;
        this.arguments = new ArrayList<>();
        this.arguments.add(target);
        this.arguments.addAll(arguments);

        this.mayBeVariableCall = mayBeVariableCall;
    }

    @Override
    public <T, C> T visit(AbstractVisitor<T, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
