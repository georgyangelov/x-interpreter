package net.gangelov.x.runtime;

import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.builtins.*;
import net.gangelov.x.runtime.classes.*;

import java.util.HashMap;
import java.util.Map;

public class Runtime {
    public final Map<String, Class> classes = new HashMap<>();

    public static final ASTClass AST_CLASS = new ASTClass();

    public final NilValue NIL;
    public final BoolValue TRUE;
    public final BoolValue FALSE;
    public final GlobalValue GLOBAL;

    public final Class IntClass, StringClass, GlobalClass;

    public Runtime() {
        Class Class = new Class("Class");
        Class Nil = new NilClass();
        Class Bool = new BoolClass();

        IntClass = new IntClass();
        StringClass = new StringClass();
        GlobalClass = new GlobalClass();

        NIL = new NilValue(Nil);
        TRUE = new BoolValue(Bool, true);
        FALSE = new BoolValue(Bool, false);
        GLOBAL = new GlobalValue(GlobalClass);

        defineClass(Class);
        defineClass(Nil);
        defineClass(Bool);
        defineClass(IntClass);
        defineClass(StringClass);
        defineClass(GlobalClass);
        defineClass(AST_CLASS);
    }

    public BoolValue from(boolean value) {
        return value ? TRUE : FALSE;
    }

    // TODO: Intern
    public IntValue from(int value) {
        return new IntValue(IntClass, value);
    }

    // TODO: Intern
    public StringValue from(String value) {
        return new StringValue(StringClass, value);
    }

    public ObjectValue createObject(Class klass) {
        return new ObjectValue(klass);
    }

    public void defineClass(Class klass) {
        classes.put(klass.name, klass);
    }

    public Class getClass(String name) {
        return classes.get(name);
    }
}
