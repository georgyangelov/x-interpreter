package net.gangelov.x.runtime.classes;

import net.gangelov.x.evaluator.Evaluator;
import net.gangelov.x.runtime.Runtime;
import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.ArrayValue;
import net.gangelov.x.runtime.builtins.IntValue;

import java.util.List;

public class ArrayClass extends Class {
    public ArrayClass(Runtime r) {
        super("Array", r, r.ObjectClass);

        defineMethod(new Method("size", 0, 0, (runtime, args) -> {
            List<Value> self = unwrap(args.get(0));

            return runtime.from(self.size());
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

        // TODO: Test these methods separately
        defineMethod(new Method("set", 2, 0, (runtime, args) -> {
            List<Value> self = unwrap(args.get(0));
            int index = unwrapIndex(args.get(1));
            Value value = args.get(2);

            self.set(index, value);

            return value;
        }));
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
