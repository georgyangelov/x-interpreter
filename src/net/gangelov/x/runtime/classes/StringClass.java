package net.gangelov.x.runtime.classes;

import net.gangelov.x.runtime.Runtime;
import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.IntValue;
import net.gangelov.x.runtime.builtins.StringValue;

public class StringClass extends Class {
    public StringClass(Runtime r) {
        super("String", r, r.ObjectClass);

        defineMethod(new Method("length", 0, 0, (runtime, args) -> {
            String self = unwrap(args.get(0));

            return runtime.wrap(self.length());
        }));

        // TODO: Make varargs
        defineMethod(new Method("concat", 1, 0, (runtime, args) -> {
            String self = unwrap(args.get(0));
            String other = unwrap(args.get(1));

            return runtime.wrap(self + other);
        }));

        defineMethod(new Method("to_i", 0, 0, (runtime, args) -> {
            String self = unwrap(args.get(0));

            return runtime.wrap(Integer.parseInt(self));
        }));

        defineMethod(new Method("to_f", 0, 0, (runtime, args) -> {
            String self = unwrap(args.get(0));

            return runtime.wrap(Double.parseDouble(self));
        }));

        defineMethod(new Method("to_s", 0, 0, (runtime, args) -> args.get(0)));
        defineMethod(new Method("inspect", 0, 0, (runtime, args) -> {
            String self = unwrap(args.get(0));

            return runtime.wrap("\"" + self + "\"");
        }));

        defineStaticMethod(new Method("hello", 0, 0, (runtime, args) -> runtime.wrap("Hello world!")));
    }

    private String unwrap(Value value) {
        return ((StringValue)value).value;
    }
}
