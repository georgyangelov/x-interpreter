package net.gangelov.x.runtime.classes;

import net.gangelov.x.runtime.base.Class;

public class NilClass extends Class {
    public NilClass(ClassClass klass, Class objectClass) {
        super("Nil", klass, objectClass);
    }
}
