package net.gangelov.x.types;

public class Type {
    public final String name;
//    public final Map<String, TypeMethod> methods;

//    public Type(String name, List<TypeMethod> methods) {
    public Type(String name) {
        this.name = name;
//        this.methods = methods.stream()
//            .collect(Collectors.toMap(
//                    method -> method.name,
//                    method -> method
//            ));
    }

    public boolean isCompatibleWith(Type other) {
        return other == this;

        //        return methods.size() == other.methods.size() && isSubtypeOf(other);
    }

//    public boolean isSubtypeOf(Type other) {
//        return other.methods.values().stream()
//                .allMatch(this::hasCompatibleMethod);
//    }
//
//    private boolean hasCompatibleMethod(TypeMethod method) {
//        TypeMethod selfMethod = methods.get(method.name);
//
//        return selfMethod != null && selfMethod.isCompatibleWith(method);
//    }
}
