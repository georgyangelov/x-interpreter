package net.gangelov.x.runtime.classes;

import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.ObjectValue;

public class ErrorClass extends Class {
    public ErrorClass(Class classClass, Class superClass) {
        super("Error", classClass, superClass);

        defineMethod(new Method("initialize", (runtime, args) -> {
            ObjectValue self = (ObjectValue)args.get(0);
            Value message;

            if (args.size() == 2) {
                message = args.get(1);
            } else {
                message = runtime.from("Unknown error " + self.getXClass().name);
            }

            self.setInstanceVariable("message", message);

            return self;
        }));

        defineMethod(new Method("message", (runtime, args) ->
                ((ObjectValue)args.get(0)).getInstanceVariable("message")
        ));
    }
}
