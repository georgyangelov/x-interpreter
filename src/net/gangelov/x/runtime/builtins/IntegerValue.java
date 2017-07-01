package net.gangelov.x.runtime.builtins;

import net.gangelov.x.ast.NumberLiteralNode;
import net.gangelov.x.runtime.Value;

public class IntegerValue extends Value {
    public final int value;

    public IntegerValue(int value) {
        this.value = value;
    }

    public IntegerValue(NumberLiteralNode node) {
        this.value = Integer.parseInt(node.str);
    }

    @Override
    public String getClassName() {
        return "Integer";
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
