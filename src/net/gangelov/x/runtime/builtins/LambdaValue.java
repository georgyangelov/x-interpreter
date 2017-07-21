package net.gangelov.x.runtime.builtins;

import net.gangelov.x.runtime.Runtime;
import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;

import java.util.List;

public class LambdaValue extends Value {
    public interface JavaMethod {
        Value call(Runtime runtime, List<Value> arguments);
    }

    private final Class klass;
    public final JavaMethod method;

    public LambdaValue(Class klass, JavaMethod method) {
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
