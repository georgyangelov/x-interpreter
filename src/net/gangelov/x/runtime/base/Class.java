package net.gangelov.x.runtime.base;

import com.sun.istack.internal.Nullable;
import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.builtins.ObjectValue;
import net.gangelov.x.runtime.builtins.StringValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Class extends Value {
    public final String name;
    private Map<String, Method> methods = new HashMap<>();

    private final Class superClass;
    protected Class classClass, staticClass;

    public Class(String name, @Nullable Class classClass, @Nullable Class superClass) {
        this.name = name;
        this.classClass = classClass;
        this.superClass = superClass;
    }

    public Class getStaticClass() {
        if (this.staticClass == null) {
            // TODO: Change this null to `superClass.getStaticClass()` but test beforehand
            this.staticClass = new Class(name + ":static", classClass, null);
        }

        return this.staticClass;
    }

    public Class getSuperClass() {
        return superClass;
    }

    public void defineMethod(Method method) {
        this.methods.put(method.name, method);
    }

    public Method getMethod(String name) {
        Method method = this.methods.get(name);

        if (method == null && superClass != null) {
            return this.superClass.getMethod(name);
        }

        return method;
    }

    public void defineStaticMethod(Method method) {
        this.getStaticClass().defineMethod(method);
    }

    public Method getStaticMethod(String name) {
        if (this.staticClass == null) {
            return null;
        }

        return this.staticClass.getMethod(name);
    }

    public boolean is(Class klass) {
        return this == klass || this.superClass != null && superClass.is(klass);
    }

    @Override
    public Class getXClass() {
        return classClass;
    }

    @Override
    public String inspect() {
        return name;
    }
}