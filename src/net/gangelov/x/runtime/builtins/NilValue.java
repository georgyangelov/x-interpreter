package net.gangelov.x.runtime.builtins;

import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;

public class NilValue extends Value {
    private final Class klass;

    public NilValue(Class klass) {
        super();

        this.klass = klass;
    }

    @Override
    public Class getXClass() {
        return klass;
    }

    @Override
    public boolean asBoolean() {
        return false;
    }
}
