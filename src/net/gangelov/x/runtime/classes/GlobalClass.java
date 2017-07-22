package net.gangelov.x.runtime.classes;

import net.gangelov.x.evaluator.XErrorException;
import net.gangelov.x.runtime.Runtime;
import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.StringValue;

public class GlobalClass extends Class {
    public GlobalClass(Runtime r) {
        super("Global", r, null);

        defineMethod(new Method("the_answer", 0, 0, (runtime, args) -> runtime.from(42)));

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
        defineMethod(new Method("puts", 1, 0, (runtime, args) -> {
            Value value = args.get(1);
            Value strValue = value.getMethod("to_s").call(runtime, value);

            if (strValue instanceof StringValue) {
                System.out.println(((StringValue) strValue).value);
            } else {
                System.out.println(strValue.inspect());
            }

            return runtime.NIL;
        }));
    }
}
