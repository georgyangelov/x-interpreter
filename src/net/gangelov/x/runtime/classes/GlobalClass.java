package net.gangelov.x.runtime.classes;

import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.IntegerValue;

public class GlobalClass extends Class {
    public GlobalClass() {
        super("Global");

        defineMethod(new Method("the_answer", args -> new IntegerValue(42)));
    }
}
