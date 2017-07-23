package net.gangelov.x.runtime.classes;

import net.gangelov.x.evaluator.Evaluator;
import net.gangelov.x.runtime.Runtime;
import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.LambdaValue;

import java.util.ArrayList;
import java.util.List;

public class LambdaClass extends Class {
    public LambdaClass(Runtime r) {
        super("Lambda", r, r.ObjectClass);

        defineStaticMethod(new Method("new", 0, 0, (runtime, args) -> {
            throw new Evaluator.RuntimeError("Cannot instantiate built-in class " + name);
        }));

        Method.JavaMethod callLambda = (runtime, args) -> {
            LambdaValue self = (LambdaValue)args.get(0);

            return self.method.call(runtime, args);
        };

        // TODO: Make actual varargs
        defineMethod(new Method("call", 0, 1000, callLambda));
        defineMethod(new Method("[]", 0, 1000, callLambda));

        defineMethod(new Method("bind", 1, 1, (runtime, args) -> {
            LambdaValue self = (LambdaValue)args.get(0);

            Value newSelf = args.get(1);
            Value newSelfClass = self.Self;

            if (args.size() > 2) {
                newSelfClass = args.get(2);
            }

            return new LambdaValue(runtime.LambdaClass, self.method, newSelf, newSelfClass);
        }));
    }

    public boolean canBeInherited() {
        return false;
    }
}
