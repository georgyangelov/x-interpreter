package net.gangelov.x.runtime.builtins;

import net.gangelov.x.runtime.Runtime;
import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;

import java.util.List;

public class LambdaValue extends Value {
    private final Class klass;
    public final Method method;

    public LambdaValue(Class klass, Method method) {
        super();

        this.klass = klass;
        this.method = method;
    }

    @Override
    public Class getXClass() {
        return klass;
    }

    @Override
    public String inspect() {
        return "#<Lambda>";
    }
}
