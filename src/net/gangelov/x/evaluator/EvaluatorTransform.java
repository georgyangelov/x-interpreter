package net.gangelov.x.evaluator;

import net.gangelov.x.ast.*;
import net.gangelov.x.ast.nodes.*;
import net.gangelov.x.runtime.Runtime;
import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.BoolValue;
import net.gangelov.x.runtime.builtins.IntValue;
import net.gangelov.x.runtime.builtins.NilValue;
import net.gangelov.x.runtime.builtins.StringValue;

import java.util.List;
import java.util.stream.Collectors;

public class EvaluatorTransform extends AbstractVisitor<Value, EvaluatorContext> {
    private final Runtime runtime;

    public EvaluatorTransform(Runtime runtime) {
        this.runtime = runtime;
    }

    @Override
    public Value visit(LiteralNode node, EvaluatorContext context) {
        switch (node.type) {
            case Nil:
                return runtime.NIL;
            case Bool:
                return runtime.from(node.str.equals("true"));
            case Int:
                return runtime.from(Integer.parseInt(node.str));
            case String:
                return runtime.from(node.str);
        }

        return null;
    }

    @Override
    public Value visit(NameNode node, EvaluatorContext context) {
        Value value = context.getLocal(node.name);

        if (value == null) {
            Class klass = runtime.getClass(node.name);

            if (klass != null) {
                return klass;
            }

            // Maybe this is a method call without arguments?
            return new MethodCallNode(node.name, new NameNode("self")).visit(this, context);
        }

        return value;
    }

    @Override
    public Value visit(AssignmentNode node, EvaluatorContext context) {
        Value value = node.value.visit(this, context);

        context.assignLocal(node.name, value);

        return value;
    }

    @Override
    public Value visit(MethodCallNode node, EvaluatorContext context) {
        List<Value> arguments = node.arguments.stream()
                .map(argument -> argument.visit(this, context))
                .collect(Collectors.toList());

        Class klass = arguments.get(0).getXClass();

        // TODO: Check method arity
        Method method = klass.getMethod(node.name);
        if (method == null) {
            throw new Evaluator.RuntimeError("No method " + node.name + " on class " + klass.name);
        }

        return method.call(runtime, arguments);
    }

    @Override
    public Value visit(BranchNode node, EvaluatorContext context) {
        Value condition = node.condition.visit(this, context);

        if (condition.asBoolean()) {
            return node.true_branch.visit(this, context);
        } else {
            return node.false_branch.visit(this, context);
        }
    }

    @Override
    public Value visit(BlockNode node, EvaluatorContext context) {
        List<Value> nodes = node.nodes.stream()
                .map(n -> n.visit(this, context))
                .collect(Collectors.toList());

        if (nodes.size() > 0) {
            return nodes.get(nodes.size() - 1);
        } else {
            return runtime.NIL;
        }
    }

    @Override
    public Value visit(WhileNode node, EvaluatorContext context) {
        Value lastValue = runtime.NIL;

        while (node.condition.visit(this, context).asBoolean()) {
            lastValue = node.body.visit(this, context.scope());
        }

        return lastValue;
    }

    @Override
    public Value visit(MethodDefinitionNode methodDefinitionNode, EvaluatorContext context) {
        Method method = new Method(methodDefinitionNode.name, (runtime, args) -> {
            List<MethodArgumentNode> formalArgs = methodDefinitionNode.arguments;

            // TODO: Should this be a scope gate?
            EvaluatorContext callContext = context.scope();

            callContext.defineLocal("self", args.get(0));

            // TODO: Check arity
            // TODO: Check types
            for (int i = 0; i < formalArgs.size(); i++) {
                callContext.defineLocal(formalArgs.get(i).name, args.get(i + 1));
            }

            return methodDefinitionNode.body.visit(this, callContext);
        });

        Class selfClass = context.getLocal("self").getXClass();
        selfClass.defineMethod(method);

        return runtime.from(methodDefinitionNode.name);
    }

    @Override
    public Value visit(MethodArgumentNode methodDefinitionNode, EvaluatorContext context) {
        throw new RuntimeException("This should not be called");
    }

    @Override
    public Value visit(ClassDefinitionNode node, EvaluatorContext context) {
        if (runtime.getClass(node.name) != null) {
            throw new Evaluator.RuntimeError("Cannot redefine class " + node.name);
        }

        // TODO: This should be a scope gate, make a new context not descending from this one
        EvaluatorContext classContext = context.scope();

        Class klass = new Class(node.name);
        runtime.defineClass(klass);

        classContext.defineLocal("self", klass);

        return node.body.visit(this, classContext);
    }
}
