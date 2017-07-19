package net.gangelov.x.runtime.builtins;

import net.gangelov.x.runtime.Value;

public class BoolValue extends Value {
    public static final BoolValue TRUE = new BoolValue(true);
    public static final BoolValue FALSE = new BoolValue(false);

    public static BoolValue from(boolean value) {
        return value ? TRUE : FALSE;
    }

    public final boolean value;

    private BoolValue(boolean value) {
        this.value = value;
    }

    @Override
    public String getClassName() {
        return "Bool";
    }

    @Override
    public String inspect() {
        return value ? "true" : "false";
    }

    @Override
    public boolean asBoolean() {
        return value;
    }
}
