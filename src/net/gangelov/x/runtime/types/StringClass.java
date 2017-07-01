package net.gangelov.x.runtime.types;

import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.IntegerValue;
import net.gangelov.x.runtime.builtins.StringValue;

import java.util.List;

public class StringClass extends Class {
    public StringClass() {
        super("String");

        defineMethod(new Method("length", (List<Value> args) -> {
            String self = getString(args.get(0));

            return new IntegerValue(self.length());
        }));

        defineMethod(new Method("concat", (List<Value> args) -> {
            String self = getString(args.get(0));
            String other = getString(args.get(1));

            return new StringValue(self + other);
        }));
    }

    private String getString(Value value) {
        return ((StringValue)value).value;
    }
}
