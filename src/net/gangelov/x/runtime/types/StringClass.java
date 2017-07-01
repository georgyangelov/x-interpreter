package net.gangelov.x.runtime.types;

import net.gangelov.x.ast.NumberLiteralNode;
import net.gangelov.x.ast.StringLiteralNode;
import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;

import java.util.List;

public class StringClass extends Class {
    public StringClass() {
        super("String");

        defineMethod(new Method("length", (List<Value> args) -> {
            String self = getString(args.get(0));

            return new NumberLiteralNode("" + self.length());
        }));

        defineMethod(new Method("concat", (List<Value> args) -> {
            String self = getString(args.get(0));
            String other = getString(args.get(1));

            return new StringLiteralNode(self + other);
        }));
    }

    private String getString(Value value) {
        return ((StringLiteralNode)value).str;
    }
}
