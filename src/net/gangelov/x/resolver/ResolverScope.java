package net.gangelov.x.resolver;

import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.classes.GlobalClass;
import net.gangelov.x.runtime.classes.IntClass;
import net.gangelov.x.runtime.classes.NilClass;
import net.gangelov.x.runtime.classes.StringClass;

import java.util.HashMap;
import java.util.Map;

public class ResolverScope {
    private final ResolverScope parent;

    private Map<String, Class> types = new HashMap<>();
    private Map<String, Resolver.VariableNode> variables = new HashMap<>();

    public ResolverScope() {
        parent = null;

        defineClass(new NilClass());
        defineClass(new IntClass());
        defineClass(new StringClass());
        defineClass(new GlobalClass());
    }

    public ResolverScope(ResolverScope parent) {
        this.parent = parent;
    }

    public void defineClass(Class type) {
        types.put(type.name, type);
    }

    public void defineVariable(Resolver.VariableNode node) {
        variables.put(node.name, node);
    }

    public Class getClass(String name) {
        Class type = types.get(name);

        if (type == null && parent != null) {
            return parent.getClass(name);
        }

        return type;
    }

    public Resolver.VariableNode getVariable(String name) {
        Resolver.VariableNode variable = variables.get(name);

        if (variable == null && parent != null) {
            return parent.getVariable(name);
        }

        return variable;
    }
}
