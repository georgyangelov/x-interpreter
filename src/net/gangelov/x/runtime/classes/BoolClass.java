package net.gangelov.x.runtime.classes;

import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.BoolValue;

public class BoolClass extends Class {
    public BoolClass(ClassClass klass, Class objectClass) {
        super("Bool", klass, objectClass);

        defineMethod(new Method("!", (runtime, args) -> {
            boolean a = getBool(args.get(0));

            return runtime.from(!a);
        }));

        defineMethod(new Method("==", (runtime, args) -> {
            boolean a = getBool(args.get(0));
            boolean b = getBool(args.get(1));

            return runtime.from(a == b);
        }));

        defineMethod(new Method("!=", (runtime, args) -> {
            boolean a = getBool(args.get(0));
            boolean b = getBool(args.get(1));

            return runtime.from(a != b);
        }));

        defineMethod(new Method("to_s", (runtime, args) -> {
            boolean self = getBool(args.get(0));

            return runtime.from(self ? "true" : "false");
        }));
    }

    private boolean getBool(Value value) {
        return ((BoolValue)value).value;
    }
}
