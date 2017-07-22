package net.gangelov.x.runtime.builtins;

import net.gangelov.x.runtime.Runtime;
import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;

import java.util.List;

public class LambdaValue extends Value {
    private final Class klass;
    public final Method method;
    public final Value self;

    public LambdaValue(Class klass, Method method, Value self) {
        super();

        this.klass = klass;
        this.method = method;
        this.self = self;
    }

    @Override
    public Class getXClass() {
        return klass;
    }
}
