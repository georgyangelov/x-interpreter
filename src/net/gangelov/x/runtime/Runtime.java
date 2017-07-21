package net.gangelov.x.runtime;

import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.builtins.*;
import net.gangelov.x.runtime.classes.*;

import java.util.HashMap;
import java.util.Map;

public class Runtime {
    public final Map<String, Class> classes = new HashMap<>();

    public final NilValue NIL;
    public final BoolValue TRUE;
    public final BoolValue FALSE;
    public final GlobalValue GLOBAL;

    public final ClassClass ClassClass;
    public final Class
            IntClass,
            StringClass,
            ErrorClass,
            GlobalClass,
            ObjectClass,
            NilClass,
            BoolClass,
            ASTClass;

    public Runtime() {
        ClassClass = new ClassClass();
        GlobalClass = new GlobalClass(ClassClass);
        ObjectClass = new ObjectClass(ClassClass, GlobalClass);

        NilClass = new NilClass(ClassClass, ObjectClass);
        BoolClass = new BoolClass(ClassClass, ObjectClass);

        IntClass = new IntClass(ClassClass, ObjectClass);
        StringClass = new StringClass(ClassClass, ObjectClass);
        ErrorClass = new ErrorClass(ClassClass, ObjectClass);

        ASTClass = new ASTClass(ClassClass, ObjectClass);

        NIL = new NilValue(NilClass);
        TRUE = new BoolValue(BoolClass, true);
        FALSE = new BoolValue(BoolClass, false);
        GLOBAL = new GlobalValue(GlobalClass);

        defineClass(ClassClass);
        defineClass(ObjectClass);
        defineClass(NilClass);
        defineClass(BoolClass);
        defineClass(ErrorClass);
        defineClass(IntClass);
        defineClass(StringClass);
        defineClass(GlobalClass);
        defineClass(ASTClass);
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
