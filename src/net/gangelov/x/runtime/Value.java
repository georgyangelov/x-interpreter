package net.gangelov.x.runtime;

import net.gangelov.x.runtime.base.Class;

public abstract class Value {
    abstract public Class getXClass();

    public String inspect() {
        return "UNKNOWN";
    }

    public boolean asBoolean() {
        return true;
    }
}
