package net.gangelov.x.runtime.classes;

import net.gangelov.x.evaluator.Evaluator;
import net.gangelov.x.runtime.Runtime;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;

public class NilClass extends Class {
    public NilClass(Runtime r) {
        super("Nil", r, r.ObjectClass);

        defineStaticMethod(new Method("new", 0, 0, (runtime, args) -> {
            throw new Evaluator.RuntimeError("Cannot instantiate built-in class " + name);
        }));

        defineMethod(new Method("inspect", 0, 0, (runtime, args) -> {
            return runtime.wrap("nil");
        }));
    }

    public boolean canBeInherited() {
        return false;
    }
}
