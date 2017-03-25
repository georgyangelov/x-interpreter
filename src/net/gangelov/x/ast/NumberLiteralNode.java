package net.gangelov.x.ast;

public class NumberLiteralNode extends LiteralNode {
    public NumberLiteralNode(String str) {
        super(str);
    }

    @Override
    public <T> void visit(Visitor<T> visitor) {
        visitor.visit(this);
    }
}
