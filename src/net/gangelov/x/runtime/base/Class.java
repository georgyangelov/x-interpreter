package net.gangelov.x.runtime.base;

import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.builtins.StringValue;

import java.util.HashMap;
import java.util.Map;

public class Class extends Value {
    public final String name;
    private Map<String, Method> methods = new HashMap<>();

    private Class staticClass;

    public Class(String name) {
        this.name = name;

        this.defineClassMethods();
    }

    public Class getStaticClass() {
        if (this.staticClass == null) {
            this.staticClass = new Class(name + ":static");
        }

        return this.staticClass;
    }

    // TODO: Use inheritance for these
    private void defineClassMethods() {
        defineMethod(new Method("class", (runtime, args) ->
                args.get(0).getXClass()));

        defineMethod(new Method("name", (runtime, args) ->
                runtime.from(args.get(0).getXClass().name)));
    }

    public void defineMethod(Method method) {
        this.methods.put(method.name, method);
    }

    public Method getMethod(String name) {
        return this.methods.get(name);
    }

    public void defineStaticMethod(Method method) {
        if (this.staticClass == null) {
            this.staticClass = new Class(name + ":static");
        }

        this.staticClass.defineMethod(method);
    }

    public Method getStaticMethod(String name) {
        if (this.staticClass == null) {
            return null;
        }

        return this.staticClass.getMethod(name);
    }

    @Override
    public Class getXClass() {
        return getStaticClass();
    }

    @Override
    public String inspect() {
        return name;
    }
}