package net.gangelov.x.debug;

import net.gangelov.x.ast.*;
import net.gangelov.x.parser.EscapeSequences;

public class ASTInspector extends Visitor {
    public static String inspect(ASTNode node) {
        ASTInspector inspector = new ASTInspector();
        node.visit(inspector, null);

        return inspector.getString();
    }

    private StringBuilder str = new StringBuilder();

    public String getString() {
        return str.toString();
    }

    @Override
    public Void visit(NumberLiteralNode node, Void context) {
        str.append(node.str);

        return null;
    }

    @Override
    public Void visit(StringLiteralNode node, Void context) {
        str.append("\"").append(EscapeSequences.convertToSequences(node.str)).append("\"");

        return null;
    }

    @Override
    public Void visit(NameNode node, Void context) {
        str.append(node.name);

        return null;
    }

    @Override
    public Void visit(MethodCallNode node, Void context) {
        str.append("(").append(node.name);

        for (ASTNode arg : node.arguments) {
            str.append(" ");
            arg.visit(this, context);
        }

        str.append(")");

        return null;
    }

    @Override
    public Void visit(BranchNode node, Void context) {
        str.append("(if ");
        node.condition.visit(this, context);

        str.append(" ");
        node.true_branch.visit(this, context);

        if (!node.false_branch.isEmpty()) {
            str.append(" ");
            node.false_branch.visit(this, context);
        }

        str.append(")");

        return null;
    }

    @Override
    public Void visit(BlockNode node, Void context) {
        str.append("{");

        for (ASTNode n : node.nodes) {
            str.append(" ");
            n.visit(this, context);
        }

        str.append(" }");

        return null;
    }

    @Override
    public Void visit(WhileNode node, Void context) {
        str.append("(while ");
        node.condition.visit(this, context);

        str.append(" ");
        node.body.visit(this, context);

        str.append(")");

        return null;
    }
}
