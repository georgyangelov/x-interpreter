package net.gangelov.x.runtime.types;

import net.gangelov.x.ast.NumberLiteralNode;
import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;

import java.util.List;

public class IntegerClass extends Class {
    public IntegerClass() {
        super("Integer");

        defineMethod(new Method("+", (List<Value> args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return new NumberLiteralNode("" + (a + b));
        }));

        defineMethod(new Method("-", (List<Value> args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return new NumberLiteralNode("" + (a - b));
        }));

        defineMethod(new Method("*", (List<Value> args) -> {
            int a = getInt(args.get(0));
            int b = getInt(args.get(1));

            return new NumberLiteralNode("" + (a * b));
        }));
    }

    private int getInt(Value value) {
        String number = ((NumberLiteralNode)value).str;

        return Integer.parseInt(number);
    }
}
