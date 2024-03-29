package net.gangelov.x.runtime;

import net.gangelov.x.evaluator.XErrorException;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.builtins.*;
import net.gangelov.x.runtime.classes.*;

import java.util.HashMap;
import java.util.List;
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
            ArrayClass,
            ASTClass;

    public Runtime() {
        // The order in which these are set is important!
        ClassClass = new ClassClass(this);
        GlobalClass = new GlobalClass(this);
        ObjectClass = new ObjectClass(this);

        NilClass = new NilClass(this);
        BoolClass = new BoolClass(this);

        IntClass = new IntClass(this);
        FloatClass = new FloatClass(this);
        StringClass = new StringClass(this);
        ErrorClass = new ErrorClass(this);

        LambdaClass = new LambdaClass(this);
        ArrayClass = new ArrayClass(this);

        ASTClass = new ASTClass(this);

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
        defineClass(FloatClass);
        defineClass(StringClass);
        defineClass(GlobalClass);
        defineClass(LambdaClass);
        defineClass(ArrayClass);
        defineClass(ASTClass);
    }

    public BoolValue wrap(boolean value) {
        return value ? TRUE : FALSE;
    }
    public FloatValue wrap(double value) {
        return new FloatValue(FloatClass, value);
    }

    // TODO: Intern
    public IntValue wrap(int value) {
        return new IntValue(IntClass, value);
    }

    // TODO: Intern
    public StringValue wrap(String value) {
        return new StringValue(StringClass, value);
    }

    public ArrayValue wrap(List<Value> list) {
        return new ArrayValue(ArrayClass, list);
    }

    public String unwrap(StringValue value) {
        return value.value;
    }

    public ObjectValue createObject(Class klass) {
        return new ObjectValue(klass);
    }

    public void raise(String message) {
        throw new XErrorException(createError(message));
    }

    public ObjectValue createError(String message) {
        return (ObjectValue)ErrorClass.getMethod("new").call(this, ErrorClass, wrap(message));
    }

    public void defineClass(Class klass) {
        classes.put(klass.name, klass);
    }

    public Class getClass(String name) {
        return classes.get(name);
    }
}
