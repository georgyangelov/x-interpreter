package net.gangelov.x.debug;

import net.gangelov.x.ast.NumberLiteralNode;
import net.gangelov.x.ast.StringLiteralNode;
import net.gangelov.x.ast.Visitor;
import net.gangelov.x.parser.EscapeSequences;

public class ASTInspector extends Visitor<Void> {
    private StringBuilder str;

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
