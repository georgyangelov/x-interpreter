package net.gangelov.x.runtime.base;

import net.gangelov.x.runtime.Value;

import java.util.List;

public class Method {
    public interface JavaMethod {
        Value call(List<Value> arguments);
    }

    public final String name, returnTypeName;
    private final JavaMethod implementation;

    public Method(String name, JavaMethod implementation, String returnTypeName) {
        this.name = name;
        this.implementation = implementation;
        this.returnTypeName = returnTypeName;
    }

    public Value call(List<Value> arguments) {
        return this.implementation.call(arguments);
    }
}
