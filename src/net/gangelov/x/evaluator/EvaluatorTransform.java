package net.gangelov.x.evaluator;

import net.gangelov.x.ast.*;
import net.gangelov.x.ast.nodes.*;
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
    @Override
    public Value visit(LiteralNode node, EvaluatorContext context) {
        switch (node.type) {
            case Nil:
                return NilValue.instance;
            case Bool:
                return BoolValue.from(node.str.equals("true"));
            case Int:
                return new IntValue(node);
            case String:
                return new StringValue(node);
        }

        return null;
    }

    @Override
    public Value visit(NameNode node, EvaluatorContext context) {
        Value value = context.getLocal(node.name);

        if (value == null) {
            // Maybe this is a method call without arguments?
            return new MethodCallNode(node.name, new NameNode("self")).visit(this, context);
        }

        return value;
    }

    @Override
    public Value visit(AssignmentNode node, EvaluatorContext context) {
        Value value = node.value.visit(this, context);

        context.defineLocal(node.name, value);

        return value;
    }

    @Override
    public Value visit(MethodCallNode node, EvaluatorContext context) {
        List<Value> arguments = node.arguments.stream()
                .map(argument -> argument.visit(this, context))
                .collect(Collectors.toList());

        String className = arguments.get(0).getClassName();

        Class klass = context.getClass(className);
        if (klass == null) {
            throw new Evaluator.RuntimeError("No class " + className);
        }

        // TODO: Check method arity
        Method method = klass.getMethod(node.name);
        if (method == null) {
            throw new Evaluator.RuntimeError("No method " + node.name + " on class " + klass.name);
        }

        return method.call(arguments);
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
            return NilValue.instance;
        }
    }

    @Override
    public Value visit(WhileNode node, EvaluatorContext context) {
        Value lastValue = NilValue.instance;

        while (node.condition.visit(this, context).asBoolean()) {
            lastValue = node.body.visit(this, context.scope());
        }

        return lastValue;
    }

    @Override
    public Value visit(MethodDefinitionNode methodDefinitionNode, EvaluatorContext context) {
        Method method = new Method(methodDefinitionNode.name, args -> {
            List<MethodArgumentNode> formalArgs = methodDefinitionNode.arguments;
            EvaluatorContext callContext = context.scope();

            callContext.defineLocal("self", args.get(0));

            // TODO: Check arity
            // TODO: Check types
            for (int i = 0; i < formalArgs.size(); i++) {
                callContext.defineLocal(formalArgs.get(i).name, args.get(i + 1));
            }

            return methodDefinitionNode.body.visit(this, callContext);
        });

        Class selfClass = context.getClass(context.getLocal("self").getClassName());
        selfClass.defineMethod(method);

        return new StringValue(methodDefinitionNode.name);
    }

    @Override
    public Value visit(MethodArgumentNode methodDefinitionNode, EvaluatorContext context) {
        throw new RuntimeException("This should not be called");
    }
}
