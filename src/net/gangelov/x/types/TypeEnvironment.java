package net.gangelov.x.types;

import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.classes.GlobalClass;
import net.gangelov.x.runtime.classes.IntClass;
import net.gangelov.x.runtime.classes.StringClass;

import java.util.HashMap;
import java.util.Map;

public class TypeEnvironment {
    private Map<String, Class> types = new HashMap<>();

    public TypeEnvironment() {
//        defineClass(new IntClass());
//        defineClass(new StringClass());
//        defineClass(new GlobalClass());
    }

    public void defineClass(Class type) {
        types.put(type.name, type);
    }

    public Class getType(String name) {
        return types.get(name);
    }
}
