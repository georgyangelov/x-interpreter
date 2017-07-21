package net.gangelov.x.runtime.classes;

import net.gangelov.x.evaluator.XErrorException;
import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.StringValue;

public class GlobalClass extends Class {
    public GlobalClass(ClassClass klass) {
        super("Global", klass, null);

        defineMethod(new Method("the_answer", (runtime, args) -> runtime.from(42)));

        defineMethod(new Method("raise", (runtime, args) -> {
            Value value = args.get(1);

            // TODO: Prevent throwing things other than instances of Error
//            throw new XErrorException(new ErrorValue(
//                    runtime.ErrorClass,
//                    runtime.from("Cannot raise object that is not an instance of Error")
//            ));

            throw new XErrorException(value);
        }));

        defineMethod(new Method("puts", (runtime, args) -> {
            Value value = args.get(1);
            Value strValue = value.getXClass().getMethod("to_s").call(runtime, value);

            if (strValue instanceof StringValue) {
                System.out.println(((StringValue) strValue).value);
            } else {
                System.out.println(strValue.inspect());
            }

            return runtime.NIL;
        }));
    }
}
