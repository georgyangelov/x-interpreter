package net.gangelov.x.runtime.classes;

import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.IntegerValue;

import java.util.List;

public class IntegerClass extends Class {
    public IntegerClass() {
        super("Integer");

        defineMethod(new Method("+", (List<Value> args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return new IntegerValue(a + b);
        }));

        defineMethod(new Method("-", (List<Value> args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return new IntegerValue(a - b);
        }));

        defineMethod(new Method("*", (List<Value> args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return new IntegerValue(a * b);
        }));
    }

    private int getInt(Value value) {
        return ((IntegerValue)value).value;
    }
}
