package net.gangelov.x.evaluator;

import net.gangelov.x.ast.*;
import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.IntegerValue;
import net.gangelov.x.runtime.builtins.NilValue;
import net.gangelov.x.runtime.builtins.StringValue;

import java.util.List;
import java.util.stream.Collectors;

public class EvaluatorTransform extends AbstractVisitor<Value, EvaluatorContext> {
    @Override
    public Value visit(NumberLiteralNode node, EvaluatorContext context) {
        return new IntegerValue(node);
    }

    @Override
    public Value visit(StringLiteralNode node, EvaluatorContext context) {
        return new StringValue(node);
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

        // TODO: Check for null
        Class klass = context.getClass(className);

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
        Value condition = node.condition.visit(this, context);
        BlockNode body = (BlockNode)node.body.visit(this, context);

//        return new WhileNode(condition, body);
        return null;
    }
}
