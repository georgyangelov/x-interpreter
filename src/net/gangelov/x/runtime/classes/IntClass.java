package net.gangelov.x.runtime.classes;

import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.IntValue;
import net.gangelov.x.runtime.builtins.NilValue;

public class IntClass extends Class {
    public IntClass() {
        super("Int");

        defineMethod(new Method("+", args -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return new IntValue(a + b);
        }));

        defineMethod(new Method("-", args -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return new IntValue(a - b);
        }));

        defineMethod(new Method("*", args -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return new IntValue(a * b);
        }));

        defineMethod(new Method("/", args -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return new IntValue(a / b);
        }));

        defineMethod(new Method("==", args -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            if (a == b) {
                return new IntValue(a);
            } else {
                return NilValue.instance;
            }
        }));
    }

    private int getInt(Value value) {
        return ((IntValue)value).value;
    }
}
