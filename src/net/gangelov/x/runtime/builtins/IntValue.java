package net.gangelov.x.runtime.builtins;

import net.gangelov.x.ast.nodes.NumberLiteralNode;
import net.gangelov.x.runtime.Value;

public class IntValue extends Value {
    public final int value;

    public IntValue(int value) {
        this.value = value;
    }

    public IntValue(NumberLiteralNode node) {
        this.value = Integer.parseInt(node.str);
    }

    @Override
    public String getClassName() {
        return "Int";
    }

    @Override
    public String inspect() {
        return Integer.toString(value);
    }

    @Override
    public boolean asBoolean() {
        return value != 0;
    }
}
