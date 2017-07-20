package net.gangelov.x.runtime.builtins;

import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ObjectValue extends Value {
    private final Class klass;
    private final Map<String, Value> instanceVariables = new HashMap<>();

    public ObjectValue(Class klass) {
        super();

        this.klass = klass;
    }

    public void setInstanceVariable(String name, Value value) {
        this.instanceVariables.put(name, value);
    }

    public Value getInstanceVariable(String name) {
        return this.instanceVariables.get(name);
    }

    @Override
    public Class getXClass() {
        return klass;
    }

    @Override
    public String inspect() {
        return "#<" + klass.name + " " + inspectInstanceVariables() + ">";
    }

    private String inspectInstanceVariables() {
        // TODO: This WILL break if there are circular references
        return this.instanceVariables.entrySet().stream()
                .map(entry -> "@" + entry.getKey() + "=" + entry.getValue().inspect())
                .collect(Collectors.joining(", "));
    }
}
