package net.gangelov.x.ast;

public class NumberLiteralNode extends LiteralNode {
    public NumberLiteralNode(String str) {
        super(str);
    }

    @Override
    public void visit(Visitor visitor) {
        visitor.visit(this);
    }
}
