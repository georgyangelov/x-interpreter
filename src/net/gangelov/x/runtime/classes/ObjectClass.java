package net.gangelov.x.runtime.classes;

import net.gangelov.x.runtime.Runtime;
import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.ObjectValue;

import java.util.stream.Collectors;

public class ObjectClass extends Class {
    public ObjectClass(Runtime r) {
        super("Object", r, r.GlobalClass);

        defineBuiltinMethods();
    }

    private void defineBuiltinMethods() {
        defineMethod(new Method("class", 0, 0, (runtime, args) -> args.get(0).getXClass()));
        defineMethod(new Method("to_s", 0, 0, (runtime, args) -> runtime.wrap(args.get(0).inspect())));

        defineMethod(new Method("inspect", 0, 0, (runtime, args) -> {
            Value self = args.get(0);
            StringBuilder str = new StringBuilder();

            str.append("#<").append(self.getXClass().name);

            if (self instanceof ObjectValue && !((ObjectValue) self).instanceVariables.isEmpty()) {
                str.append(" ").append(inspectInstanceVariables((ObjectValue)self));
            }

            str.append(">");

            return runtime.wrap(str.toString());
        }));
    }

    private String inspectInstanceVariables(ObjectValue object) {
        // TODO: This WILL break if there are circular references
        return object.instanceVariables.entrySet().stream()
                .map(entry -> "@" + entry.getKey() + "=" + entry.getValue().inspect())
                .collect(Collectors.joining(", "));
    }
}
