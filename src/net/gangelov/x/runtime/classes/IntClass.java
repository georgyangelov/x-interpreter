package net.gangelov.x.runtime.classes;

import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.BoolValue;
import net.gangelov.x.runtime.builtins.IntValue;

public class IntClass extends Class {
    public IntClass(ClassClass klass, Class objectClass) {
        super("Int", klass, objectClass);

        defineMethod(new Method("+", (runtime, args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return runtime.from(a + b);
        }));

        defineMethod(new Method("-", (runtime, args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return runtime.from(a - b);
        }));

        defineMethod(new Method("*", (runtime, args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return runtime.from(a * b);
        }));

        defineMethod(new Method("/", (runtime, args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return runtime.from(a / b);
        }));

        defineMethod(new Method("==", (runtime, args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return runtime.from(a == b);
        }));

        defineMethod(new Method(">", (runtime, args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return runtime.from(a > b);
        }));

        defineMethod(new Method("<", (runtime, args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return runtime.from(a < b);
        }));

        defineMethod(new Method("<=", (runtime, args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return runtime.from(a <= b);
        }));

        defineMethod(new Method(">=", (runtime, args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return runtime.from(a >= b);
        }));
    }

    private int getInt(Value value) {
        return ((IntValue)value).value;
    }
}
