package net.gangelov.x.runtime.base;

import net.gangelov.x.runtime.Runtime;
import net.gangelov.x.runtime.Value;

import java.util.Arrays;
import java.util.List;

public class Method {
    public interface JavaMethod {
        Value call(Runtime runtime, List<Value> arguments);
    }

    public final String name;
    private final JavaMethod implementation;

    public Method(String name, JavaMethod implementation) {
        this.name = name;
        this.implementation = implementation;
    }

    public Value call(Runtime runtime, List<Value> arguments) {
        return this.implementation.call(runtime, arguments);
    }

    public Value call(Runtime runtime, Value... arguments) {
        List<Value> args = Arrays.asList(arguments);

        return this.implementation.call(runtime, args);
    }
}
