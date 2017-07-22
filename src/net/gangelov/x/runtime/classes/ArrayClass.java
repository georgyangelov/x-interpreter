package net.gangelov.x.runtime.classes;

import net.gangelov.x.evaluator.Evaluator;
import net.gangelov.x.runtime.Runtime;
import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.ArrayValue;
import net.gangelov.x.runtime.builtins.IntValue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ArrayClass extends Class {
    public ArrayClass(Runtime r) {
        super("Array", r, r.ObjectClass);

        // TODO: Make real varargs
        defineStaticMethod(new Method("new", 0, 1000, (runtime, args) -> {
            List<Value> elements = new ArrayList<>();
            elements.addAll(args.subList(1, args.size()));

            return runtime.wrap(elements);
        }));

        defineMethod(new Method("size", 0, 0, (runtime, args) -> {
            List<Value> self = unwrap(args.get(0));

            return runtime.wrap(self.size());
        }));

        defineMethod(new Method("push", 1, 0, (runtime, args) -> {
            List<Value> self = unwrap(args.get(0));

            self.add(args.get(1));

            return args.get(0);
        }));

        defineMethod(new Method("get", 1, 0, (runtime, args) -> {
            List<Value> self = unwrap(args.get(0));
            int index = unwrapIndex(args.get(1));

            return self.get(index);
        }));

        defineMethod(new Method("[]", 1, 0, (runtime, args) -> {
            List<Value> self = unwrap(args.get(0));
            int index = unwrapIndex(args.get(1));

            return self.get(index);
        }));

        // TODO: Test these methods separately
        defineMethod(new Method("set", 2, 0, (runtime, args) -> {
            List<Value> self = unwrap(args.get(0));
            int index = unwrapIndex(args.get(1));
            Value value = args.get(2);

            self.set(index, value);

            return value;
        }));

        defineMethod(new Method("inspect", 0, 0, (runtime, args) -> {
            String values = unwrap(args.get(0)).stream()
                    .map(Value::inspect)
                    .collect(Collectors.joining(", "));

            return runtime.wrap("[" + values + "]");
        }));
    }

    public boolean canBeInherited() {
        return false;
    }

    private List<Value> unwrap(Value value) {
        return ((ArrayValue)value).list;
    }

    private int unwrapIndex(Value value) {
        if (!(value instanceof IntValue)) {
            throw new Evaluator.RuntimeError("Array indices must be integers");
        }

        return ((IntValue)value).value;
    }
}
