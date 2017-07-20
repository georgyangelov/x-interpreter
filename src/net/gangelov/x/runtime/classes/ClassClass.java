package net.gangelov.x.runtime.classes;

import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.ObjectValue;

import java.util.ArrayList;
import java.util.List;

public class ClassClass extends Class {
    public ClassClass() {
        // TODO: Inherit from Object?
        super("Class", null, null);
        this.classClass = this;

        defineBuiltinMethods();
    }

    private void defineBuiltinMethods() {
        // TODO: Inherit from Object and this will come naturally
        defineMethod(new Method("class", (runtime, args) ->
                args.get(0).getXClass()
        ));

        defineMethod(new Method("superclass", (runtime, args) ->
                args.get(0).getXClass().getSuperClass()
        ));

        defineMethod(new Method("new", (runtime, args) -> {
            ObjectValue instance = runtime.createObject((Class)args.get(0));
            Method initializer = instance.getXClass().getMethod("initialize");

            if (initializer != null) {
                List<Value> initializeArgs = new ArrayList<>();
                initializeArgs.addAll(args);
                initializeArgs.set(0, instance);

                initializer.call(runtime, initializeArgs);
            }

            return instance;
        }));

        defineStaticMethod(new Method("name", (runtime, args) ->
                runtime.from(((Class)args.get(0)).name)
        ));
    }
}
