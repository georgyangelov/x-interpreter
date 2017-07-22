package net.gangelov.x.runtime.classes;

import net.gangelov.x.evaluator.Evaluator;
import net.gangelov.x.runtime.Runtime;
import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.BoolValue;
import net.gangelov.x.runtime.builtins.IntValue;

public class IntClass extends Class {
    public IntClass(Runtime r) {
        super("Int", r, r.ObjectClass);

        defineStaticMethod(new Method("new", 0, 0, (runtime, args) -> {
            throw new Evaluator.RuntimeError("Cannot instantiate built-in class " + name);
        }));

        defineMethod(new Method("+", 1, 0, (runtime, args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return runtime.wrap(a + b);
        }));

        defineMethod(new Method("-", 1, 0, (runtime, args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return runtime.wrap(a - b);
        }));

        defineMethod(new Method("*", 1, 0, (runtime, args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return runtime.wrap(a * b);
        }));

        defineMethod(new Method("/", 1, 0, (runtime, args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return runtime.wrap(a / b);
        }));

        defineMethod(new Method("==", 1, 0, (runtime, args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return runtime.wrap(a == b);
        }));

        defineMethod(new Method(">", 1, 0, (runtime, args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return runtime.wrap(a > b);
        }));

        defineMethod(new Method("<", 1, 0, (runtime, args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return runtime.wrap(a < b);
        }));

        defineMethod(new Method("<=", 1, 0, (runtime, args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return runtime.wrap(a <= b);
        }));

        defineMethod(new Method(">=", 1, 0, (runtime, args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return runtime.wrap(a >= b);
        }));

        defineMethod(new Method("mod", 1, 0, (runtime, args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return runtime.wrap(a % b);
        }));

        defineMethod(new Method("to_f", 0, 0, (runtime, args) -> {
            int self = getInt(args.get(0));

            return runtime.wrap((double)self);
        }));

        defineMethod(new Method("to_i", 0, 0, (runtime, args) -> args.get(0)));

        defineMethod(new Method("inspect", 0, 0, (runtime, args) -> {
            int self = getInt(args.get(0));

            return runtime.wrap("" + self);
        }));
    }

    public boolean canBeInherited() {
        return false;
    }

    private int getInt(Value value) {
        return ((IntValue)value).value;
    }
}
