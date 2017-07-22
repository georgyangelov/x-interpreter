package net.gangelov.x.runtime.classes;

import net.gangelov.x.evaluator.Evaluator;
import net.gangelov.x.runtime.Runtime;
import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.IntValue;
import net.gangelov.x.runtime.builtins.StringValue;

public class StringClass extends Class {
    public StringClass(Runtime r) {
        super("String", r, r.ObjectClass);

        defineStaticMethod(new Method("new", 0, 0, (runtime, args) -> {
            throw new Evaluator.RuntimeError("Cannot instantiate built-in class " + name);
        }));

        defineMethod(new Method("length", 0, 0, (runtime, args) -> {
            String self = unwrap(args.get(0));

            return runtime.wrap(self.length());
        }));

        // TODO: Make varargs
        defineMethod(new Method("concat", 1, 1000, (runtime, args) -> {
            String self = unwrap(args.get(0));
            StringBuilder str = new StringBuilder();

            args.forEach(arg -> {
                str.append(unwrap(arg));
            });

            return runtime.wrap(str.toString());
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

    public boolean canBeInherited() {
        return false;
    }

    private String unwrap(Value value) {
        return ((StringValue)value).value;
    }
}
