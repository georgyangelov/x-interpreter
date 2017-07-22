package net.gangelov.x.runtime.classes;

import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.FloatValue;
import net.gangelov.x.runtime.builtins.IntValue;

public class FloatClass extends Class {
    public FloatClass(ClassClass klass, Class objectClass) {
        super("Float", klass, objectClass);

        defineMethod(new Method("+", (runtime, args) -> {
            double a = getFloat(args.get(0));
            double b = getFloat(args.get(1));

            return runtime.from(a + b);
        }));

        defineMethod(new Method("-", (runtime, args) -> {
            double a = getFloat(args.get(0));
            double b = getFloat(args.get(1));

            return runtime.from(a - b);
        }));

        defineMethod(new Method("*", (runtime, args) -> {
            double a = getFloat(args.get(0));
            double b = getFloat(args.get(1));

            return runtime.from(a * b);
        }));

        defineMethod(new Method("/", (runtime, args) -> {
            double a = getFloat(args.get(0));
            double b = getFloat(args.get(1));

            return runtime.from(a / b);
        }));

        defineMethod(new Method("==", (runtime, args) -> {
            double a = getFloat(args.get(0));
            double b = getFloat(args.get(1));

            return runtime.from(a == b);
        }));

        defineMethod(new Method(">", (runtime, args) -> {
            double a = getFloat(args.get(0));
            double b = getFloat(args.get(1));

            return runtime.from(a > b);
        }));

        defineMethod(new Method("<", (runtime, args) -> {
            double a = getFloat(args.get(0));
            double b = getFloat(args.get(1));

            return runtime.from(a < b);
        }));

        defineMethod(new Method("<=", (runtime, args) -> {
            double a = getFloat(args.get(0));
            double b = getFloat(args.get(1));

            return runtime.from(a <= b);
        }));

        defineMethod(new Method(">=", (runtime, args) -> {
            double a = getFloat(args.get(0));
            double b = getFloat(args.get(1));

            return runtime.from(a >= b);
        }));

        defineMethod(new Method("mod", (runtime, args) -> {
            double a = getFloat(args.get(0));
            double b = getFloat(args.get(1));

            return runtime.from(a % b);
        }));

        defineMethod(new Method("to_f", (runtime, args) -> args.get(0)));

        defineMethod(new Method("to_i", (runtime, args) -> {
            double self = getFloat(args.get(0));

            return runtime.from((int)self);
        }));

        defineMethod(new Method("to_s", (runtime, args) -> {
            double self = getFloat(args.get(0));

            return runtime.from("" + self);
        }));
    }

    private double getFloat(Value value) {
        return ((FloatValue)value).value;
    }
}
