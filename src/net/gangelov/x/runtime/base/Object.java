package net.gangelov.x.runtime.base;

import java.util.List;

public class Object extends Value {
    private Class klass;

    public Object(Class klass) {
        this.klass = klass;
    }

    public Value send(String name, List<Value> args) {
        return klass.getMethod(name).call(this, args);
    }

    public boolean respondsTo(String name) {
        return klass.getMethod(name) != null;
    }
}
