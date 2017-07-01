package net.gangelov.x.runtime;

public abstract class Value {
    // TODO: Return class instance?
    abstract public String getClassName();

    public String inspect() {
        return "UNKNOWN";
    }

    public boolean asBoolean() {
        return true;
    }
}
