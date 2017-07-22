package net.gangelov.x.runtime.builtins;

import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;

public class StringValue extends Value {
    public final String value;
    private final Class klass;

    public StringValue(Class klass, String value) {
        this.klass = klass;
        this.value = value;
    }

    @Override
    public Class getXClass() {
        return klass;
    }
}
