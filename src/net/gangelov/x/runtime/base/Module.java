package net.gangelov.x.runtime.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Module extends Value {
    // TODO: Make these private and use accessor methods
    private final Map<String, Method> methods = new HashMap<>();
    private final Map<String, Value> constants = new HashMap<>();

    public void defineMethod(String name, Method method) {
        methods.put(name, method);
    }

    public void defineConstant(String name, Value value) {
        constants.put(name, value);
    }

    public <T> T getConstant(java.lang.Class<T> type, String name) {
        return (T)constants.get(name);
    }

    public Method getMethod(String name) {
        return methods.get(name);
    }
}
