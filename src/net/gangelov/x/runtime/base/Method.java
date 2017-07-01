package net.gangelov.x.runtime.base;

import net.gangelov.x.runtime.Value;

import java.util.List;

public class Method {
    public interface JavaMethod {
        Value call(List<Value> arguments);
    }

    public final String name;
    private final JavaMethod implementation;

    public Method(String name, JavaMethod implementation) {
        this.name = name;
        this.implementation = implementation;
    }

    public Value call(List<Value> arguments) {
        return this.implementation.call(arguments);
    }
}
