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

    private final Class Int, String;

    public Runtime() {
        Class Class = new Class("Class");
        Class Nil = new NilClass();
        Class Bool = new BoolClass();
        Class Global = new GlobalClass();

        Int = new IntClass();
        String = new StringClass();

        NIL = new NilValue(Nil);
        TRUE = new BoolValue(Bool, true);
        FALSE = new BoolValue(Bool, false);
        GLOBAL = new GlobalValue(Global);

        defineClass(Class);
        defineClass(Nil);
        defineClass(Bool);
        defineClass(Int);
        defineClass(String);
        defineClass(Global);
        defineClass(AST_CLASS);
    }

    public BoolValue from(boolean value) {
        return value ? TRUE : FALSE;
    }

    // TODO: Intern
    public IntValue from(int value) {
        return new IntValue(Int, value);
    }

    // TODO: Intern
    public StringValue from(String value) {
        return new StringValue(String, value);
    }

    public void defineClass(Class klass) {
        classes.put(klass.name, klass);
    }

    public Class getClass(String name) {
        return classes.get(name);
    }
}
