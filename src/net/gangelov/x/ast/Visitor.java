package net.gangelov.x.ast;

public class Visitor<T> {
    protected T defaultReturnValue() {
        return null;
    }

    public T visit(NumberLiteralNode node) {
        return defaultReturnValue();
    }

    public T visit(StringLiteralNode node) {
        return defaultReturnValue();
    }
}
