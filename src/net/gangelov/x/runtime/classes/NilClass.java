package net.gangelov.x.runtime.classes;

import net.gangelov.x.runtime.Runtime;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;

public class NilClass extends Class {
    public NilClass(Runtime r) {
        super("Nil", r, r.ObjectClass);

        defineMethod(new Method("inspect", 0, 0, (runtime, args) -> {
            return runtime.wrap("nil");
        }));
    }
}
