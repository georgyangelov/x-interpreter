package net.gangelov.x.debug;

import net.gangelov.x.ast.ASTNode;
import net.gangelov.x.ast.NumberLiteralNode;
import net.gangelov.x.ast.StringLiteralNode;
import net.gangelov.x.ast.Visitor;
import net.gangelov.x.parser.EscapeSequences;

public class ASTInspector extends Visitor<Void> {
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
    public Void visit(NumberLiteralNode node) {
        str.append(node.str);

        return super.visit(node);
    }

    @Override
    public Void visit(StringLiteralNode node) {
        str.append("\"").append(EscapeSequences.convertToSequences(node.str)).append("\"");

        return super.visit(node);
    }
}
