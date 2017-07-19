package net.gangelov.x.runtime.builtins;

import net.gangelov.x.ast.nodes.LiteralNode;
import net.gangelov.x.runtime.Value;

public class StringValue extends Value {
    public final String value;

    public StringValue(String value) {
        this.value = value;
    }

    public StringValue(LiteralNode node) {
        this.value = node.str;
    }

    @Override
    public String getClassName() {
        return "String";
    }

    @Override
    public String inspect() {
        return "\"" + value + "\"";
    }
}
