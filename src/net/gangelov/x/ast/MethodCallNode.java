package net.gangelov.x.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    @Override
    public void visit(Visitor visitor) {
        visitor.visit(this);
    }
}
