package net.gangelov.x.runtime.classes;

import net.gangelov.x.runtime.Runtime;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;

public class ObjectClass extends Class {
    public ObjectClass(Runtime r) {
        super("Object", r, r.GlobalClass);

        defineBuiltinMethods();
    }

    private void defineBuiltinMethods() {
        defineMethod(new Method("class", 0, 0, (runtime, args) -> args.get(0).getXClass()));
        defineMethod(new Method("to_s", 0, 0, (runtime, args) -> runtime.from(args.get(0).inspect())));
        defineMethod(new Method("inspect", 0, 0, (runtime, args) -> runtime.from(args.get(0).inspect())));
    }
}
