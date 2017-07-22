package net.gangelov.x.runtime.classes;

import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.IntValue;
import net.gangelov.x.runtime.builtins.StringValue;

public class StringClass extends Class {
    public StringClass(ClassClass klass, Class objectClass) {
        super("String", klass, objectClass);

        defineMethod(new Method("length", 0, 0, (runtime, args) -> {
            String self = getString(args.get(0));

            return runtime.from(self.length());
        }));

        // TODO: Make varargs
        defineMethod(new Method("concat", 1, 0, (runtime, args) -> {
            String self = getString(args.get(0));
            String other = getString(args.get(1));

            return runtime.from(self + other);
        }));

        defineMethod(new Method("to_i", 0, 0, (runtime, args) -> {
            String self = getString(args.get(0));

            return runtime.from(Integer.parseInt(self));
        }));

        defineMethod(new Method("to_f", 0, 0, (runtime, args) -> {
            String self = getString(args.get(0));

            return runtime.from(Double.parseDouble(self));
        }));

        defineMethod(new Method("to_s", 0, 0, (runtime, args) -> args.get(0)));

        defineStaticMethod(new Method("hello", 0, 0, (runtime, args) -> runtime.from("Hello world!")));
    }

    private String getString(Value value) {
        return ((StringValue)value).value;
    }
}
