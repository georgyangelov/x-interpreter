package net.gangelov.x.runtime.base;

import net.gangelov.x.evaluator.Evaluator;
import net.gangelov.x.evaluator.XErrorException;
import net.gangelov.x.runtime.Runtime;
import net.gangelov.x.runtime.Value;

import java.util.Arrays;
import java.util.List;

public class Method {
    public interface JavaMethod {
        Value call(Runtime runtime, List<Value> arguments);
    }

    public final String name;
    public final int requiredArgs, optionalArgs;
    private final JavaMethod implementation;

    public Method(String name, int requiredArgs, int optionalArgs, JavaMethod implementation) {
        this.name = name;
        this.requiredArgs = requiredArgs;
        this.optionalArgs = optionalArgs;
        this.implementation = implementation;
    }

    public Value call(Runtime runtime, List<Value> arguments) {
        int givenArguments = arguments.size() - 1;

        if (givenArguments < requiredArgs || givenArguments > requiredArgs + optionalArgs) {
            if (optionalArgs == 0) {
                throw new Evaluator.RuntimeError(
                        "Invalid number of arguments for method " + name + ". " +
                        "Expected " + requiredArgs + ", got " + givenArguments
                );
            } else {
                throw new Evaluator.RuntimeError(
                        "Invalid number of arguments for method " + name + ". " +
                        "Expected " + requiredArgs + ".." + (requiredArgs + optionalArgs) +
                        ", got " + givenArguments
                );
            }
        }

        return this.implementation.call(runtime, arguments);
    }

    public Value call(Runtime runtime, Value... arguments) {
        List<Value> args = Arrays.asList(arguments);

        return this.implementation.call(runtime, args);
    }
}
