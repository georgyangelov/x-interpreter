package net.gangelov.x.debug;

import net.gangelov.x.ast.*;
import net.gangelov.x.parser.EscapeSequences;

public class ASTInspector extends Visitor {
    public static String inspect(ASTNode node) {
        ASTInspector inspector = new ASTInspector();
        node.visit(inspector);

        return inspector.getString();
    }

    private StringBuilder str = new StringBuilder();

    public String getString() {
        return str.toString();
    }

    @Override
    public void visit(NumberLiteralNode node) {
        str.append(node.str);

        super.visit(node);
    }

    @Override
    public void visit(StringLiteralNode node) {
        str.append("\"").append(EscapeSequences.convertToSequences(node.str)).append("\"");

        super.visit(node);
    }

    @Override
    public void visit(NameNode node) {
        str.append(node.name);

        super.visit(node);
    }

    @Override
    public void visit(MethodCallNode node) {
        str.append("(").append(node.name);

        for (ASTNode arg : node.arguments) {
            str.append(" ");
            arg.visit(this);
        }

        str.append(")");
    }
}
