package net.gangelov.x.ast;

import java.util.ArrayList;
import java.util.List;

public class MethodCallNode extends ASTNode {
    public final String name;
    public final List<ASTNode> arguments;

    public MethodCallNode(String name, List<ASTNode> arguments) {
        super();
        this.name = name;
        this.arguments = arguments;
    }

    public MethodCallNode(String name, ASTNode left, ASTNode right) {
        super();
        this.name = name;

        arguments = new ArrayList<>();
        arguments.add(left);
        arguments.add(right);
    }

    @Override
    public void visit(Visitor visitor) {
        visitor.visit(this);
    }
}
