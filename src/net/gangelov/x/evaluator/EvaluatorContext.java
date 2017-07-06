package net.gangelov.x.evaluator;

import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;

import java.util.HashMap;
import java.util.Map;

public class EvaluatorContext {
    private final EvaluatorContext parent;

    private final Map<String, Class> classes = new HashMap<>();
    private final Map<String, Value> locals = new HashMap<>();

    public EvaluatorContext() {
        parent = null;
    }

    public EvaluatorContext(EvaluatorContext parent) {
        this.parent = parent;
    }

    public EvaluatorContext scope() {
        return new EvaluatorContext(this);
    }

    public void defineClass(Class klass) {
        classes.put(klass.name, klass);
    }

    public Class getClass(String name) {
        Class klass = classes.get(name);

        if (klass == null && parent != null) {
            klass = parent.getClass(name);
        }

        return klass;
    }

    public void defineLocal(String name, Value value) {
        locals.put(name, value);
    }

    public Value getLocal(String name) {
        Value value = locals.get(name);

        if (value == null && parent != null) {
            value = parent.getLocal(name);
        }

        return value;
    }
}
