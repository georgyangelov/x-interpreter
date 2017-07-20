package net.gangelov.x.runtime.builtins;

import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;

// TODO: Instance variables
public class ObjectValue extends Value {
    private final Class klass;

    public ObjectValue(Class klass) {
        super();

        this.klass = klass;
    }

    @Override
    public Class getXClass() {
        return klass;
    }
}
