package net.gangelov.x.types;

import java.util.List;
import java.util.stream.IntStream;

public class TypeMethod {
    public final String name;
    public final List<Type> arguments;
    public final Type returnType;

    public TypeMethod(String name, List<Type> arguments, Type returnType) {
        this.name = name;
        this.arguments = arguments;
        this.returnType = returnType;
    }

    public boolean isCompatibleWith(TypeMethod other) {
        return other == this;

        //        return returnType.isCompatibleWith(other.returnType) &&
//                arguments.size() == other.arguments.size() &&
//                IntStream.range(0, arguments.size())
//                        .allMatch(i -> arguments.get(i).isCompatibleWith(other.arguments.get(i)));
    }
}
