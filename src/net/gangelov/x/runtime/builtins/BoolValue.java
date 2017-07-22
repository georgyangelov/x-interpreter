package net.gangelov.x.runtime.builtins;

import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;

public class BoolValue extends Value {
    private final Class klass;
    public final boolean value;

    public BoolValue(Class klass, boolean value) {
        this.value = value;
        this.klass = klass;
    }

    @Override
    public Class getXClass() {
        return klass;
    }

    @Override
    public boolean asBoolean() {
        return value;
    }
}
