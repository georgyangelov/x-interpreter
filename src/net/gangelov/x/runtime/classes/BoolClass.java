package net.gangelov.x.runtime.classes;

import net.gangelov.x.evaluator.Evaluator;
import net.gangelov.x.runtime.Runtime;
import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.BoolValue;

public class BoolClass extends Class {
    public BoolClass(Runtime r) {
        super("Bool", r, r.ObjectClass);

        defineStaticMethod(new Method("new", 0, 0, (runtime, args) -> {
            throw new Evaluator.RuntimeError("Cannot instantiate built-in class " + name);
        }));

        defineMethod(new Method("!", 0, 0, (runtime, args) -> {
            boolean a = unwrap(args.get(0));

            return runtime.wrap(!a);
        }));

        defineMethod(new Method("==", 1, 0, (runtime, args) -> {
            boolean a = unwrap(args.get(0));
            boolean b = unwrap(args.get(1));

            return runtime.wrap(a == b);
        }));

        defineMethod(new Method("!=", 1, 0, (runtime, args) -> {
            boolean a = unwrap(args.get(0));
            boolean b = unwrap(args.get(1));

            return runtime.wrap(a != b);
        }));

        defineMethod(new Method("to_s", 0, 0, (runtime, args) -> {
            boolean self = unwrap(args.get(0));

            return runtime.wrap(self ? "true" : "false");
        }));

        defineMethod(new Method("inspect", 0, 0, (runtime, args) -> {
            boolean self = unwrap(args.get(0));

            return runtime.wrap(self ? "true" : "false");
        }));
    }

    public boolean canBeInherited() {
        return false;
    }

    private boolean unwrap(Value value) {
        return ((BoolValue)value).value;
    }
}
