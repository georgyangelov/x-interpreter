package net.gangelov.x.runtime.builtins;

import net.gangelov.x.runtime.Value;

public class NilValue extends Value {
    public static final NilValue instance = new NilValue();

    protected NilValue() {
        super();
    }

    @Override
    public String getClassName() {
        return "Nil";
    }

    @Override
    public String inspect() {
        return "nil";
    }

    @Override
    public boolean asBoolean() {
        return false;
    }
}
