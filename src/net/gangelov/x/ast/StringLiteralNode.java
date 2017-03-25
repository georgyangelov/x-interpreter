package net.gangelov.x.ast;

public class StringLiteralNode extends LiteralNode {
    public StringLiteralNode(String str) {
        super(str);
    }

    @Override
    public <T> void visit(Visitor<T> visitor) {
        visitor.visit(this);
    }
}
