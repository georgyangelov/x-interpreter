package net.gangelov.x.runtime;

import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.builtins.*;
import net.gangelov.x.runtime.classes.*;

import java.util.HashMap;
import java.util.Map;

public class Runtime {
    public final Map<String, Class> classes = new HashMap<>();

    public static final ClassClass CLASS = new ClassClass();
    public static final ObjectClass OBJECT = new ObjectClass(CLASS);
    public static final ASTClass AST_CLASS = new ASTClass(CLASS, OBJECT);

    public final NilValue NIL;
    public final BoolValue TRUE;
    public final BoolValue FALSE;
    public final GlobalValue GLOBAL;

    public final Class IntClass, StringClass, GlobalClass, ErrorClass;

    public Runtime() {
        Class Nil = new NilClass(CLASS, OBJECT);
        Class Bool = new BoolClass(CLASS, OBJECT);

        IntClass = new IntClass(CLASS, OBJECT);
        StringClass = new StringClass(CLASS, OBJECT);
        GlobalClass = new GlobalClass(CLASS, OBJECT);
        ErrorClass = new ErrorClass(CLASS, OBJECT);

        NIL = new NilValue(Nil);
        TRUE = new BoolValue(Bool, true);
        FALSE = new BoolValue(Bool, false);
        GLOBAL = new GlobalValue(GlobalClass);

        defineClass(CLASS);
        defineClass(OBJECT);
        defineClass(Nil);
        defineClass(Bool);
        defineClass(ErrorClass);
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
