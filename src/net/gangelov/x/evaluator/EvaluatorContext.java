package net.gangelov.x.evaluator;

import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.classes.IntegerClass;
import net.gangelov.x.runtime.classes.StringClass;

import java.util.HashMap;
import java.util.Map;

public class EvaluatorContext {
    private final Map<String, Class> classes = new HashMap<>();

    public EvaluatorContext() {
        defineClass(new IntegerClass());
        defineClass(new StringClass());
    }

    public void defineClass(Class klass) {
        classes.put(klass.name, klass);
    }

    public Class getClass(String name) {
        return classes.get(name);
    }
}
