package net.gangelov.x.runtime.builtins;

import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;

public class FloatValue extends Value {
    public final double value;
    private final Class klass;

    public FloatValue(Class klass, double value) {
        this.klass = klass;
        this.value = value;
    }

    @Override
    public Class getXClass() {
        return klass;
    }

    @Override
    public boolean asBoolean() {
        return true;
    }
}
