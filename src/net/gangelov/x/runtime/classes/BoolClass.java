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
            boolean a = getBool(args.get(0));

            return runtime.from(!a);
        }));

        defineMethod(new Method("==", 1, 0, (runtime, args) -> {
            boolean a = getBool(args.get(0));
            boolean b = getBool(args.get(1));

            return runtime.from(a == b);
        }));

        defineMethod(new Method("!=", 1, 0, (runtime, args) -> {
            boolean a = getBool(args.get(0));
            boolean b = getBool(args.get(1));

            return runtime.from(a != b);
        }));

        defineMethod(new Method("to_s", 0, 0, (runtime, args) -> {
            boolean self = getBool(args.get(0));

            return runtime.from(self ? "true" : "false");
        }));
    }

    private boolean getBool(Value value) {
        return ((BoolValue)value).value;
    }
}
