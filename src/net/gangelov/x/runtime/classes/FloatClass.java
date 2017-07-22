package net.gangelov.x.runtime.classes;

import net.gangelov.x.runtime.Runtime;
import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.FloatValue;
import net.gangelov.x.runtime.builtins.IntValue;

public class FloatClass extends Class {
    public FloatClass(Runtime r) {
        super("Float", r, r.ObjectClass);

        defineMethod(new Method("+", 1, 0, (runtime, args) -> {
            double a = getFloat(args.get(0));
            double b = getFloat(args.get(1));

            return runtime.from(a + b);
        }));

        defineMethod(new Method("-", 1, 0, (runtime, args) -> {
            double a = getFloat(args.get(0));
            double b = getFloat(args.get(1));

            return runtime.from(a - b);
        }));

        defineMethod(new Method("*", 1, 0, (runtime, args) -> {
            double a = getFloat(args.get(0));
            double b = getFloat(args.get(1));

            return runtime.from(a * b);
        }));

        defineMethod(new Method("/", 1, 0, (runtime, args) -> {
            double a = getFloat(args.get(0));
            double b = getFloat(args.get(1));

            return runtime.from(a / b);
        }));

        defineMethod(new Method("==", 1, 0, (runtime, args) -> {
            double a = getFloat(args.get(0));
            double b = getFloat(args.get(1));

            return runtime.from(a == b);
        }));

        defineMethod(new Method(">", 1, 0, (runtime, args) -> {
            double a = getFloat(args.get(0));
            double b = getFloat(args.get(1));

            return runtime.from(a > b);
        }));

        defineMethod(new Method("<", 1, 0, (runtime, args) -> {
            double a = getFloat(args.get(0));
            double b = getFloat(args.get(1));

            return runtime.from(a < b);
        }));

        defineMethod(new Method("<=", 1, 0, (runtime, args) -> {
            double a = getFloat(args.get(0));
            double b = getFloat(args.get(1));

            return runtime.from(a <= b);
        }));

        defineMethod(new Method(">=", 1, 0, (runtime, args) -> {
            double a = getFloat(args.get(0));
            double b = getFloat(args.get(1));

            return runtime.from(a >= b);
        }));

        defineMethod(new Method("mod", 1, 0, (runtime, args) -> {
            double a = getFloat(args.get(0));
            double b = getFloat(args.get(1));

            return runtime.from(a % b);
        }));

        defineMethod(new Method("to_f", 0, 0, (runtime, args) -> args.get(0)));

        defineMethod(new Method("to_i", 0, 0, (runtime, args) -> {
            double self = getFloat(args.get(0));

            return runtime.from((int)self);
        }));

        defineMethod(new Method("to_s", 0, 0, (runtime, args) -> {
            double self = getFloat(args.get(0));

            return runtime.from("" + self);
        }));
    }

    private double getFloat(Value value) {
        return ((FloatValue)value).value;
    }
}
