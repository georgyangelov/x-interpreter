package net.gangelov.x.runtime.classes;

import net.gangelov.x.runtime.Runtime;
import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.ObjectValue;

public class ErrorClass extends Class {
    public ErrorClass(Runtime r) {
        super("Error", r, r.ObjectClass);

        defineMethod(new Method("initialize", 0, 1, (runtime, args) -> {
            ObjectValue self = (ObjectValue)args.get(0);
            Value message;

            if (args.size() == 2) {
                message = args.get(1);
            } else {
                message = runtime.wrap("Unknown error " + self.getXClass().name);
            }

            self.setInstanceVariable("message", message);

            return self;
        }));

        defineMethod(new Method("message", 0, 0, (runtime, args) ->
                ((ObjectValue)args.get(0)).getInstanceVariable("message")
        ));
    }
}
