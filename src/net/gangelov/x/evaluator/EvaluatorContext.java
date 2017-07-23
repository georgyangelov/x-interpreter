package net.gangelov.x.evaluator;

import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.builtins.ObjectValue;

import java.util.HashMap;
import java.util.Map;

public class EvaluatorContext {
    private final EvaluatorContext parent;

    private final Map<String, Value> locals = new HashMap<>();
    private String currentMethodName;

    public EvaluatorContext() {
        parent = null;
    }

    public EvaluatorContext(EvaluatorContext parent) {
        this.parent = parent;
    }

    public EvaluatorContext scope() {
        return new EvaluatorContext(this);
    }

    public void setCurrentMethodName(String name) {
        this.currentMethodName = name;
    }

    public String getCurrentMethodName() {
        if (currentMethodName != null) {
            return currentMethodName;
        }

        if (parent != null) {
            return parent.getCurrentMethodName();
        }

        return null;
    }

    public void defineLocal(String name, Value value) {
        locals.put(name, value);
    }

    public void assignLocal(String name, Value value) {
        if (name.startsWith("@")) {
            getSelfInstance().setInstanceVariable(name, value);
            return;
        }

        if (!setIfDefined(name, value)) {
            locals.put(name, value);
        }
    }

    private ObjectValue getSelfInstance() {
        Value self = getLocal("self");
        if (!(self instanceof ObjectValue)) {
            throw new Evaluator.RuntimeError("Cannot access instance variable on non-object");
        }

        return (ObjectValue)self;
    }

    private boolean setIfDefined(String name, Value value) {
        if (locals.containsKey(name)) {
            locals.put(name, value);
            return true;
        }

        return parent != null && parent.setIfDefined(name, value);
    }

    public Value getLocal(String name) {
        if (name.startsWith("@")) {
            return getSelfInstance().getInstanceVariable(name);
        }

        Value value = locals.get(name);

        if (value == null && parent != null) {
            value = parent.getLocal(name);
        }

        return value;
    }
}
