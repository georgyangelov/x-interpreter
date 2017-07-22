package net.gangelov.x.runtime.builtins;

import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;

import java.util.List;
import java.util.stream.Collectors;

public class ArrayValue extends Value {
    private final Class klass;
    public final List<Value> list;

    public ArrayValue(Class klass, List<Value> list) {
        super();

        this.klass = klass;
        this.list = list;
    }

    @Override
    public Class getXClass() {
        return klass;
    }

    @Override
    public String inspect() {
        String values = list.stream()
                .map(Value::inspect)
                .collect(Collectors.joining(", "));

        return "[" + values + "]";
    }
}
