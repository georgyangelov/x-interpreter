package net.gangelov.x.runtime.base;

import com.sun.istack.internal.Nullable;
import net.gangelov.x.runtime.Runtime;
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

    protected final Class superClass;
    public final Runtime runtime;
    protected Class staticClass;

    public Class(String name, Runtime runtime, Class superClass) {
        this.name = name;
        this.runtime = runtime;
        this.superClass = superClass;
    }

    public boolean canBeInherited() {
        return true;
    }

    public Class getStaticClass() {
        if (staticClass == null) {
            Class parentStaticClass = null;

            if (superClass != null) {
                parentStaticClass = superClass.getStaticClass();
            }

            this.staticClass = new Class(name + ":static", runtime, parentStaticClass);
        }

        return this.staticClass;
    }

    public Class getSuperClass() {
        return superClass;
    }

    public void defineMethod(Method method) {
        this.methods.put(method.name, method);
    }

    public Method getInstanceMethod(String name) {
        Method method = this.methods.get(name);

        if (method == null && getSuperClass() != null) {
            return getSuperClass().getInstanceMethod(name);
        }

        return method;
    }

    public void defineStaticMethod(Method method) {
        getStaticClass().defineMethod(method);
    }

    public Method getStaticMethod(String name) {
        return getStaticClass().getInstanceMethod(name);
    }

    @Override
    public Method getMethod(String name) {
        Method method = getStaticMethod(name);

        if (method == null) {
            method = getXClass().getInstanceMethod(name);
        }

        return method;
    }

    public boolean is(Class klass) {
        return this == klass || getSuperClass() != null && getSuperClass().is(klass);
    }

    @Override
    public Class getXClass() {
        return runtime.ClassClass;
    }
}