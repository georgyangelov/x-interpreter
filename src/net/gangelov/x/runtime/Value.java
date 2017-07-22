package net.gangelov.x.runtime;

import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.StringValue;

public abstract class Value {
    abstract public Class getXClass();

    public Method getMethod(String name) {
        return getXClass().getInstanceMethod(name);
    }

    public String inspect() {
        Method inspect = getMethod("inspect");

        if (inspect == null) {
            return "UNKNOWN";
        }

        Value repr = getMethod("inspect").call(getXClass().runtime, this);

        if (repr instanceof StringValue) {
            return ((StringValue) repr).value;
        } else {
            return repr.inspect();
        }
    }

    public boolean asBoolean() {
        return true;
    }

    public boolean instanceOf(Class klass) {
        return getXClass().is(klass);
    }
}
