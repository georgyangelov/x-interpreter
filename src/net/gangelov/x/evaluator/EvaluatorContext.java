package net.gangelov.x.evaluator;

import net.gangelov.x.runtime.IntegerClass;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.base.Module;
import net.gangelov.x.runtime.base.Value;
import net.gangelov.x.runtime.interop.JavaMethod;

public class EvaluatorContext {
    public Module global = new Module();

    public EvaluatorContext() {
        global.defineConstant("Integer", new IntegerClass());
        global.defineMethod("new_int", new JavaMethod((self, args) -> {
            return global.getConstant(IntegerClass.class, "Integer").createInstance();
        }));
    }
}
