package net.gangelov.x.evaluator;

import net.gangelov.x.ast.*;
import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.IntegerValue;
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
        return node;
    }

    @Override
    public Value visit(MethodCallNode node, EvaluatorContext context) {
        List<Value> arguments = node.arguments.stream()
                .map(argument -> argument.visit(this, context))
                .collect(Collectors.toList());

        // TODO: Optimize type detection, do not do it multiple times for the same object
        String className = arguments.get(0).getClassName();

        // TODO: Check for null
        Class klass = context.getClass(className);

        // TODO: Check if method exists
        // TODO: Check method arity
        Method method = klass.getMethod(node.name);

        return method.call(arguments);
    }

    @Override
    public Value visit(BranchNode node, EvaluatorContext context) {
        Value condition = node.condition.visit(this, context);
        BlockNode true_branch = (BlockNode)node.true_branch.visit(this, context);
        BlockNode false_branch = (BlockNode)node.false_branch.visit(this, context);

//        return new BranchNode(condition, true_branch, false_branch);
        return null;
    }

    @Override
    public BlockNode visit(BlockNode node, EvaluatorContext context) {
        List<Value> nodes = node.nodes.stream()
                .map(n -> n.visit(this, context))
                .collect(Collectors.toList());

//        return new BlockNode(nodes);
        return null;
    }

    @Override
    public Value visit(WhileNode node, EvaluatorContext context) {
        Value condition = node.condition.visit(this, context);
        BlockNode body = (BlockNode)node.body.visit(this, context);

//        return new WhileNode(condition, body);
        return null;
    }
}
