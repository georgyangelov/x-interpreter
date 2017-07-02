package net.gangelov.x.runtime.builtins;

import net.gangelov.x.runtime.Value;

public class GlobalValue extends Value {
    public static final GlobalValue instance = new GlobalValue();

    protected GlobalValue() {
        super();
    }

    @Override
    public String getClassName() {
        return "Global";
    }

    @Override
    public String inspect() {
        return "global";
    }
}
