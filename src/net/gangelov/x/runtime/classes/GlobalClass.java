package net.gangelov.x.runtime.classes;

import net.gangelov.x.evaluator.XErrorException;
import net.gangelov.x.runtime.Runtime;
import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.StringValue;

import java.util.stream.Collectors;

public class GlobalClass extends Class {
    public GlobalClass(Runtime r) {
        super("Global", r, null);

        defineMethod(new Method("the_answer", 0, 0, (runtime, args) -> runtime.wrap(42)));

        defineMethod(new Method("raise", 1, 0, (runtime, args) -> {
            Value value = args.get(1);

            // TODO: Prevent throwing things other than instances of Error
//            throw new XErrorException(new ErrorValue(
//                    runtime.ErrorClass,
//                    runtime.from("Cannot raise object that is not an instance of Error")
//            ));

            throw new XErrorException(value);
        }));

        // TODO: Make varargs
        defineMethod(new Method("puts", 1, 1000, (runtime, args) -> {
            String output = args.stream()
                    .skip(1)
                    .map(value -> {
                        return value.getMethod("to_s").call(runtime, value);
                    })
                    .map(value -> {
                        if (value instanceof StringValue) {
                            return ((StringValue) value).value;
                        } else {
                            return value.inspect();
                        }
                    })
                    .collect(Collectors.joining(""));

            System.out.println(output);

            return runtime.NIL;
        }));

        defineMethod(new Method("inspect", 0, 0, (runtime, args) -> {
            return runtime.wrap("GLOBAL");
        }));
    }
}
