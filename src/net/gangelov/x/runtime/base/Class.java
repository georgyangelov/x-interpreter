package net.gangelov.x.runtime.base;

import java.util.List;

public class Class extends Module {
    public Value createInstance(List<Value> args) {
        Object instance = new Object(this);

        if (instance.respondsTo("initialize")) {
            instance.send("initialize", args);
        }

        return instance;
    }
}
