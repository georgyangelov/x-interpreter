package net.gangelov.x.resolver;

import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Resolver {
    public static class ResolveError extends Exception {
        public ResolveError(String message) {
            super(message);
        }
    }

    public static abstract class Node {
        public Class type = null;
        public List<Effect> effects = new ArrayList<>();
    }

    public static class ConstantNode extends Node {
        public ConstantNode(Class type) {
            super();

            this.type = type;
        }
    }

    public static class VariableNode extends Node {
        public final String name;

        public VariableNode(String name) {
            super();

            this.name = name;
        }
    }

    public static class MethodCallNode extends Node {
        public final String name;

        public MethodCallNode(String name) {
            super();

            this.name = name;
        }
    }

    public static abstract class Effect {
        public final Node source, target;

        public Effect(Node source, Node target) {
            this.source = source;
            this.target = target;
        }

        abstract public boolean apply() throws ResolveError;
    }

    public static class AssignmentEffect extends Effect {
        public AssignmentEffect(Node source, Node target) {
            super(source, target);
        }

        public boolean apply() throws ResolveError {
            if (source.type == target.type) {
                return false;
            } else if (target.type != null) {
                throw new ResolveError(
                        "Type " + source.type.name + " cannot be assigned to " + target.type.name
                );
            }

            target.type = source.type;

            return true;
        }
    }

    public static class ArgumentAssignmentEffect extends AssignmentEffect {
        public final int argIndex;

        public ArgumentAssignmentEffect(Node source, Node target, int argIndex) {
            super(source, target);

            this.argIndex = argIndex;
        }

        public boolean apply() throws ResolveError {
            MethodCallNode call = (MethodCallNode)target;

            if (argIndex == 0) {
                Method method = source.type.getMethod(call.name);

                if (method.returnType == target.type) {
                    return false;
                } else if (target.type != null) {
                    // TODO: Error
                    return false;
                }

                target.type = method.returnType;

                return true;
            } else {
                // TODO
                return false;
            }
        }
    }

    private List<Node> nodes = new ArrayList<>();
    private List<Effect> effects = new ArrayList<>();

    public ConstantNode defineConstant(Class type) {
        ConstantNode node = new ConstantNode(type);

        nodes.add(node);

        return node;
    }

    public VariableNode defineVariable(String name) {
        VariableNode node = new VariableNode(name);

        nodes.add(node);

        return node;
    }

    public MethodCallNode defineMethodCall(String name) {
        MethodCallNode node = new MethodCallNode(name);

        nodes.add(node);

        return node;
    }

    public void assign(Node node, VariableNode variable) {
        Effect effect = new AssignmentEffect(node, variable);

        node.effects.add(effect);

        effects.add(effect);
    }

    public MethodCallNode methodCall(String name, List<Node> arguments) {
        MethodCallNode call = defineMethodCall(name);

        for (int i = 0; i < arguments.size(); i++) {
            Node arg = arguments.get(i);

            effects.add(new ArgumentAssignmentEffect(arg, call, i));
        }

        return call;
    }

    public void resolve() throws ResolveError {
        Queue<Effect> queue = new ArrayDeque<>();

        nodes.stream()
                .filter(node -> node.type != null)
                .forEach(node -> queue.addAll(node.effects));

        Effect effect;
        while ((effect = queue.poll()) != null) {
            // TODO: Is it certain that the effect can be applied?
            boolean updated = effect.apply();

            if (updated && effect.target.type != null) {
                queue.addAll(effect.target.effects);
            }
        }
    }
}
