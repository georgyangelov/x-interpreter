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
            double a = unwrap(args.get(0));
            double b = unwrap(args.get(1));

            return runtime.wrap(a + b);
        }));

        defineMethod(new Method("-", 1, 0, (runtime, args) -> {
            double a = unwrap(args.get(0));
            double b = unwrap(args.get(1));

            return runtime.wrap(a - b);
        }));

        defineMethod(new Method("*", 1, 0, (runtime, args) -> {
            double a = unwrap(args.get(0));
            double b = unwrap(args.get(1));

            return runtime.wrap(a * b);
        }));

        defineMethod(new Method("/", 1, 0, (runtime, args) -> {
            double a = unwrap(args.get(0));
            double b = unwrap(args.get(1));

            return runtime.wrap(a / b);
        }));

        defineMethod(new Method("==", 1, 0, (runtime, args) -> {
            double a = unwrap(args.get(0));
            double b = unwrap(args.get(1));

            return runtime.wrap(a == b);
        }));

        defineMethod(new Method(">", 1, 0, (runtime, args) -> {
            double a = unwrap(args.get(0));
            double b = unwrap(args.get(1));

            return runtime.wrap(a > b);
        }));

        defineMethod(new Method("<", 1, 0, (runtime, args) -> {
            double a = unwrap(args.get(0));
            double b = unwrap(args.get(1));

            return runtime.wrap(a < b);
        }));

        defineMethod(new Method("<=", 1, 0, (runtime, args) -> {
            double a = unwrap(args.get(0));
            double b = unwrap(args.get(1));

            return runtime.wrap(a <= b);
        }));

        defineMethod(new Method(">=", 1, 0, (runtime, args) -> {
            double a = unwrap(args.get(0));
            double b = unwrap(args.get(1));

            return runtime.wrap(a >= b);
        }));

        defineMethod(new Method("mod", 1, 0, (runtime, args) -> {
            double a = unwrap(args.get(0));
            double b = unwrap(args.get(1));

            return runtime.wrap(a % b);
        }));

        defineMethod(new Method("to_f", 0, 0, (runtime, args) -> args.get(0)));

        defineMethod(new Method("to_i", 0, 0, (runtime, args) -> {
            double self = unwrap(args.get(0));

            return runtime.wrap((int)self);
        }));

        defineMethod(new Method("inspect", 0, 0, (runtime, args) -> {
            double self = unwrap(args.get(0));

            return runtime.wrap("" + self);
        }));
    }

    private double unwrap(Value value) {
        return ((FloatValue)value).value;
    }
}
