package net.gangelov.x.runtime.classes;

import net.gangelov.x.runtime.Runtime;
import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.BoolValue;

public class BoolClass extends Class {
    public BoolClass(Runtime r) {
        super("Bool", r, r.ObjectClass);

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

    private boolean unwrap(Value value) {
        return ((BoolValue)value).value;
    }
}
