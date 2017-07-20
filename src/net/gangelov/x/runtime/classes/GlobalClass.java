package net.gangelov.x.runtime.classes;

import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.IntValue;

public class GlobalClass extends Class {
    public GlobalClass(ClassClass klass, Class objectClass) {
        super("Global", klass, objectClass);

        defineMethod(new Method("the_answer", (runtime, args) -> runtime.from(42)));
    }
}
