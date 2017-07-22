package net.gangelov.x.runtime.builtins;

import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;

public class GlobalValue extends Value {
    private final Class klass;

    public GlobalValue(Class klass) {
        super();

        this.klass = klass;
    }

    @Override
    public Class getXClass() {
        return klass;
    }

//    @Override
//    public String inspect() {
//        return "global";
//    }
}
