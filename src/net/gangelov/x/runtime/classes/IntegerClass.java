package net.gangelov.x.runtime.classes;

import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.IntegerValue;
import net.gangelov.x.runtime.builtins.NilValue;

import java.util.List;

public class IntegerClass extends Class {
    public IntegerClass() {
        super("Integer");

        defineMethod(new Method("+", args -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return new IntegerValue(a + b);
        }));

        defineMethod(new Method("-", args -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return new IntegerValue(a - b);
        }));

        defineMethod(new Method("*", args -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return new IntegerValue(a * b);
        }));

        defineMethod(new Method("/", args -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return new IntegerValue(a / b);
        }));

        defineMethod(new Method("==", args -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            if (a == b) {
                return new IntegerValue(a);
            } else {
                return NilValue.instance;
            }
        }));
    }

    private int getInt(Value value) {
        return ((IntegerValue)value).value;
    }
}
