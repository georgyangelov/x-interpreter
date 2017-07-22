package net.gangelov.x.runtime.classes;

import net.gangelov.x.runtime.Runtime;
import net.gangelov.x.runtime.base.Class;

public class NilClass extends Class {
    public NilClass(Runtime r) {
        super("Nil", r, r.ObjectClass);
    }
}
