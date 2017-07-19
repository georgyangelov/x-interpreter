package net.gangelov.x.runtime.classes;

import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.IntValue;
import net.gangelov.x.runtime.builtins.StringValue;

public class StringClass extends Class {
    public StringClass() {
        super("String");

        defineMethod(new Method("length", (runtime, args) -> {
            String self = getString(args.get(0));

            return runtime.from(self.length());
        }));

        defineMethod(new Method("concat", (runtime, args) -> {
            String self = getString(args.get(0));
            String other = getString(args.get(1));

            return runtime.from(self + other);
        }));

        defineStaticMethod(new Method("hello", (runtime, args) -> runtime.from("Hello world!")));
    }

    private String getString(Value value) {
        return ((StringValue)value).value;
    }
}
