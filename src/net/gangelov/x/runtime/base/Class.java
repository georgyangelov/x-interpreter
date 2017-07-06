package net.gangelov.x.runtime.base;

import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.builtins.StringValue;

import java.util.HashMap;
import java.util.Map;

public class Class {
    public final String name;
    private Map<String, Method> methods = new HashMap<>();

    public Class(String name) {
        this.name = name;

        defineMethod(new Method("class", args -> new StringValue(args.get(0).getClassName())));
    }

    public void defineMethod(Method method) {
        this.methods.put(method.name, method);
    }

    public Method getMethod(String name) {
        return this.methods.get(name);
    }
}
