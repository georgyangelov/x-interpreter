package net.gangelov.x.runtime;

import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;

public abstract class Value {
    abstract public Class getXClass();

    public Method getMethod(String name) {
        return getXClass().getInstanceMethod(name);
    }

    public String inspect() {
        return "UNKNOWN";
    }

    public boolean asBoolean() {
        return true;
    }

    public boolean instanceOf(Class klass) {
        return getXClass().is(klass);
    }
}
