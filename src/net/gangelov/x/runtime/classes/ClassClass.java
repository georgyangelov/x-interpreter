package net.gangelov.x.runtime.classes;

import net.gangelov.x.evaluator.Evaluator;
import net.gangelov.x.runtime.Runtime;
import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.LambdaValue;
import net.gangelov.x.runtime.builtins.ObjectValue;
import net.gangelov.x.runtime.builtins.StringValue;

import java.util.ArrayList;
import java.util.List;

public class ClassClass extends Class {
    public ClassClass(Runtime r) {
        // TODO: Inherit from Object?
        super("Class", r, null);

        defineBuiltinMethods();
    }

    private void defineBuiltinMethods() {
        // TODO: Inherit from Object and this will come naturally
        defineMethod(new Method("class", 0, 0, (runtime, args) ->
                args.get(0).getXClass()
        ));

        defineMethod(new Method("superclass", 0, 0, (runtime, args) -> {
            Class klass = ((Class)args.get(0)).getSuperClass();

            if (klass == null) {
                return runtime.NIL;
            }

            return klass;
        }));

        // TODO: The 1000 optional args here are a workaround for varargs
        defineMethod(new Method("new", 0, 1000, (runtime, args) -> {
            Class klass = (Class)args.get(0);

            ObjectValue instance = runtime.createObject(klass);
            Method initializer = instance.getMethod("initialize");

            if (initializer != null) {
                List<Value> initializeArgs = new ArrayList<>();
                initializeArgs.addAll(args);
                initializeArgs.set(0, instance);

                initializer.call(runtime, initializeArgs);
            }

            return instance;
        }));

        defineStaticMethod(new Method("new", 0, 2, (runtime, args) -> {
            String name = "Anonymous";
            Class superClass = runtime.ObjectClass;

            // TODO: Test
            if (args.size() > 1) {
                name = ((StringValue)args.get(1)).value;
            }

            // TODO: Test
            if (args.size() > 2) {
                superClass = (Class)args.get(2);
            }

            // TODO: Test
            if (!superClass.canBeInherited()) {
                throw new Evaluator.RuntimeError("Cannot inherit from built-in class " + superClass.name);
            }

            Class newClass = new Class(name, runtime, superClass);

            // TODO: Test
            if (!name.equals("Anonymous")) {
                runtime.defineClass(newClass);
            }

            return newClass;
        }));

        defineMethod(new Method("static", 1, 0, (runtime, args) -> {
            Class klass = (Class)args.get(0);
            Value maybeBlock = args.get(1);

            if (!(maybeBlock instanceof LambdaValue)) {
                throw new Evaluator.RuntimeError("The `static` method must be passed a lambda");
            }

            LambdaValue boundBlock = new LambdaValue(
                    runtime.LambdaClass,
                    ((LambdaValue)maybeBlock).method,
                    klass,
                    klass.getStaticClass()
            );

            return boundBlock.getMethod("call").call(runtime, boundBlock);
        }));

        // TODO: Test
        defineMethod(new Method("name", 0, 0, (runtime, args) ->
                runtime.wrap(((Class)args.get(0)).name)
        ));

        defineMethod(new Method("inspect", 0, 0, (runtime, args) -> {
            return runtime.wrap(((Class)args.get(0)).name);
        }));
    }
}
