package net.gangelov.x.runtime.builtins;

import net.gangelov.x.ast.nodes.LiteralNode;
import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;

public class IntValue extends Value {
    public final int value;
    private final Class klass;

    public IntValue(Class klass, int value) {
        this.klass = klass;
        this.value = value;
    }

    @Override
    public Class getXClass() {
        return klass;
    }

//    @Override
//    public String inspect() {
//        return Integer.toString(value);
//    }
}
