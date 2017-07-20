package net.gangelov.x.runtime.classes;

import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;

public class ObjectClass extends Class {
    public ObjectClass(Class classClass) {
        super("Object", classClass, null);

        defineBuiltinMethods();
    }

    private void defineBuiltinMethods() {
        defineMethod(new Method("class", (runtime, args) -> args.get(0).getXClass()));
    }
}
