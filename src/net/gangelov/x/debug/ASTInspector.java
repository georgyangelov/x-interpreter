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

    @Override
    public void visit(BranchNode node) {
        str.append("(if ");
        node.condition.visit(this);

        str.append(" ");
        node.true_branch.visit(this);

        if (!node.false_branch.isEmpty()) {
            str.append(" ");
            node.false_branch.visit(this);
        }

        str.append(")");
    }

    @Override
    public void visit(BlockNode node) {
        str.append("{");

        for (ASTNode n : node.nodes) {
            str.append(" ");
            n.visit(this);
        }

        str.append(" }");
    }

    @Override
    public void visit(WhileNode node) {
        str.append("(while ");
        node.condition.visit(this);

        str.append(" ");
        node.body.visit(this);

        str.append(")");
    }
}
