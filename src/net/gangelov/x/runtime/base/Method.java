package net.gangelov.x.runtime.base;

import java.util.List;

public abstract class Method {
    public abstract Value call(Value self, List<Value> args);
}
