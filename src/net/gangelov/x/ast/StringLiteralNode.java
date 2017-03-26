package net.gangelov.x.ast;

public class StringLiteralNode extends LiteralNode {
    public StringLiteralNode(String str) {
        super(str);
    }

    @Override
    public void visit(Visitor visitor) {
        visitor.visit(this);
    }
}
