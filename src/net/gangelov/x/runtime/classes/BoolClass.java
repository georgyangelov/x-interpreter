package net.gangelov.x.runtime.classes;

import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.BoolValue;

public class BoolClass extends Class {
    public BoolClass() {
        super("Bool");

        defineMethod(new Method("!", args -> {
            boolean a = getBool(args.get(0));

            return BoolValue.from(!a);
        }));

        defineMethod(new Method("==", args -> {
            boolean a = getBool(args.get(0));
            boolean b = getBool(args.get(1));

            return BoolValue.from(a == b);
        }));

        defineMethod(new Method("!=", args -> {
            boolean a = getBool(args.get(0));
            boolean b = getBool(args.get(1));

            return BoolValue.from(a != b);
        }));
    }

    private boolean getBool(Value value) {
        return ((BoolValue)value).value;
    }
}
