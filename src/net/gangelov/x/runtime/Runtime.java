package net.gangelov.x.runtime;

import net.gangelov.x.evaluator.XErrorException;
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
            FloatClass,
            StringClass,
            ErrorClass,
            GlobalClass,
            ObjectClass,
            NilClass,
            BoolClass,
            LambdaClass,
            ASTClass;

    public Runtime() {
        ClassClass = new ClassClass();
        GlobalClass = new GlobalClass(ClassClass);
        ObjectClass = new ObjectClass(ClassClass, GlobalClass);

        NilClass = new NilClass(ClassClass, ObjectClass);
        BoolClass = new BoolClass(ClassClass, ObjectClass);

        IntClass = new IntClass(ClassClass, ObjectClass);
        FloatClass = new FloatClass(ClassClass, ObjectClass);
        StringClass = new StringClass(ClassClass, ObjectClass);
        ErrorClass = new ErrorClass(ClassClass, ObjectClass);

        LambdaClass = new LambdaClass(ClassClass, ObjectClass);

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
        defineClass(LambdaClass);
        defineClass(ASTClass);
    }

    public BoolValue from(boolean value) {
        return value ? TRUE : FALSE;
    }
    public FloatValue from(double value) {
        return new FloatValue(FloatClass, value);
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

    public void raise(String message) {
        throw new XErrorException(createError(message));
    }

    public ObjectValue createError(String message) {
        return (ObjectValue)ClassClass.getMethod("new").call(this, ErrorClass, from(message));
    }

    public void defineClass(Class klass) {
        classes.put(klass.name, klass);
    }

    public Class getClass(String name) {
        return classes.get(name);
    }
}
