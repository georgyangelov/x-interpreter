package net.gangelov.x.runtime.classes;

import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.LambdaValue;

public class LambdaClass extends Class {
    public LambdaClass(Class classClass, Class superClass) {
        super("Lambda", classClass, superClass);

        defineMethod(new Method("call", (runtime, args) -> {
            LambdaValue self = (LambdaValue)args.get(0);

            return self.method.call(runtime, args);
        }));
    }
}
