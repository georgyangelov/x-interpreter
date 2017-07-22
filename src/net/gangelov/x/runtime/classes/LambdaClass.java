package net.gangelov.x.runtime.classes;

import net.gangelov.x.evaluator.Evaluator;
import net.gangelov.x.runtime.Runtime;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.LambdaValue;

public class LambdaClass extends Class {
    public LambdaClass(Runtime r) {
        super("Lambda", r, r.ObjectClass);

        defineStaticMethod(new Method("new", 0, 0, (runtime, args) -> {
            throw new Evaluator.RuntimeError("Cannot instantiate built-in class " + name);
        }));

        // TODO: Make actual varargs
        defineMethod(new Method("call", 0, 1000, (runtime, args) -> {
            LambdaValue self = (LambdaValue)args.get(0);

            return self.method.call(runtime, args);
        }));
    }

    public boolean canBeInherited() {
        return false;
    }
}
