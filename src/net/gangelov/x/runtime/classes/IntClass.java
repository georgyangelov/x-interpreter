package net.gangelov.x.runtime.classes;

import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.BoolValue;
import net.gangelov.x.runtime.builtins.IntValue;

public class IntClass extends Class {
    public IntClass(ClassClass klass, Class objectClass) {
        super("Int", klass, objectClass);

        defineMethod(new Method("+", 1, 0, (runtime, args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return runtime.from(a + b);
        }));

        defineMethod(new Method("-", 1, 0, (runtime, args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return runtime.from(a - b);
        }));

        defineMethod(new Method("*", 1, 0, (runtime, args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return runtime.from(a * b);
        }));

        defineMethod(new Method("/", 1, 0, (runtime, args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return runtime.from(a / b);
        }));

        defineMethod(new Method("==", 1, 0, (runtime, args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return runtime.from(a == b);
        }));

        defineMethod(new Method(">", 1, 0, (runtime, args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return runtime.from(a > b);
        }));

        defineMethod(new Method("<", 1, 0, (runtime, args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return runtime.from(a < b);
        }));

        defineMethod(new Method("<=", 1, 0, (runtime, args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return runtime.from(a <= b);
        }));

        defineMethod(new Method(">=", 1, 0, (runtime, args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return runtime.from(a >= b);
        }));

        defineMethod(new Method("mod", 1, 0, (runtime, args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return runtime.from(a % b);
        }));

        defineMethod(new Method("to_f", 0, 0, (runtime, args) -> {
            int self = getInt(args.get(0));

            return runtime.from((double)self);
        }));

        defineMethod(new Method("to_i", 0, 0, (runtime, args) -> args.get(0)));

        defineMethod(new Method("to_s", 0, 0, (runtime, args) -> {
            int self = getInt(args.get(0));

            return runtime.from("" + self);
        }));
    }

    private int getInt(Value value) {
        return ((IntValue)value).value;
    }
}
