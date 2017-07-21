package net.gangelov.x.evaluator;

import net.gangelov.x.ast.*;
import net.gangelov.x.ast.nodes.*;
import net.gangelov.x.runtime.Runtime;
import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EvaluatorTransform extends AbstractVisitor<Value, EvaluatorContext> {
    private final Runtime runtime;

    public EvaluatorTransform(Runtime runtime) {
        this.runtime = runtime;
    }

    private ObjectValue getSelfInstance(EvaluatorContext context) {
        Value self = context.getLocal("self");
        if (!(self instanceof ObjectValue)) {
            throw new Evaluator.RuntimeError("Cannot access instance variable on non-object");
        }

        return (ObjectValue)self;
    }

    @Override
    public Value visit(LiteralNode node, EvaluatorContext context) {
        // TODO: Move these to the runtime
        node.xClass = runtime.ASTClass;

        switch (node.type) {
            case Nil:
                return runtime.NIL;
            case Bool:
                return runtime.from(node.str.equals("true"));
            case Int:
                return runtime.from(Integer.parseInt(node.str));
            case String:
                return runtime.from(node.str);
            case Float:
                return runtime.from(Double.parseDouble(node.str));
        }

        return null;
    }

    @Override
    public Value visit(NameNode node, EvaluatorContext context) {
        node.xClass = runtime.ASTClass;

        Value value;

        if (node.name.startsWith("@")) {
            value = getSelfInstance(context).getInstanceVariable(node.name);

            if (value == null) {
                throw new Evaluator.RuntimeError("Undefined instance variable " + node.name);
            }
        } else {
            value = context.getLocal(node.name);
        }

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
        node.xClass = runtime.ASTClass;

        Value value = node.value.visit(this, context);

        if (node.name.startsWith("@")) {
            getSelfInstance(context).setInstanceVariable(node.name, value);
        } else {
            context.assignLocal(node.name, value);
        }

        return value;
    }

    @Override
    public Value visit(MethodCallNode node, EvaluatorContext context) {
        node.xClass = runtime.ASTClass;

        List<Value> arguments = node.arguments.stream()
                .map(argument -> argument.visit(this, context))
                .collect(Collectors.toList());

        Class klass = arguments.get(0).getXClass();
        String methodName = node.name;

        if (methodName.equals("super")) {
            if (context.getCurrentMethodName() == null) {
                throw new Evaluator.RuntimeError("Cannot call super outside of a method");
            }

            Class superclass = klass.getSuperClass();
            if (superclass == null) {
                throw new Evaluator.RuntimeError(
                        "Cannot call super because " + klass.name + " does not have a superclass"
                );
            }

            klass = superclass;
            methodName = context.getCurrentMethodName();
        }

        // TODO: Check method arity
        Method method = klass.getMethod(methodName);
        if (method == null) {
            throw new Evaluator.RuntimeError("No method " + methodName + " on class " + klass.name);
        }

        return method.call(runtime, arguments);
    }

    @Override
    public Value visit(BranchNode node, EvaluatorContext context) {
        node.xClass = runtime.ASTClass;

        Value condition = node.condition.visit(this, context);

        if (condition.asBoolean()) {
            return node.true_branch.visit(this, context);
        } else {
            return node.false_branch.visit(this, context);
        }
    }

    @Override
    public Value visit(BlockNode node, EvaluatorContext context) {
        node.xClass = runtime.ASTClass;

        // TODO: Set another context? What about the if blocks and defining variables there?

        try {
            List<Value> nodes = node.nodes.stream()
                    .map(n -> n.visit(this, context))
                    .collect(Collectors.toList());

            if (nodes.size() > 0) {
                return nodes.get(nodes.size() - 1);
            } else {
                return runtime.NIL;
            }
        } catch (XErrorException exception) {
            Value error = exception.error;

            Optional<CatchNode> maybeCaughtNode = node.catchNodes.stream()
                    .filter(catchNode -> canCatchError(catchNode, error))
                    .findFirst();

            if (maybeCaughtNode.isPresent()) {
                CatchNode caughtNode = maybeCaughtNode.get();
                EvaluatorContext catchContext = context.scope();

                if (caughtNode.name != null) {
                    catchContext.defineLocal(caughtNode.name, error);
                }

                return caughtNode.body.visit(this, catchContext);
            }

            throw exception;
        }
    }

    private boolean canCatchError(CatchNode node, Value error) {
        node.xClass = runtime.ASTClass;

        Class expectedClass = runtime.getClass(node.klass);
        if (expectedClass == null) {
            // TODO: Test
            throw new Evaluator.RuntimeError("No class named " + node.klass);
        }

        return error.instanceOf(expectedClass);
    }

    @Override
    public Value visit(WhileNode node, EvaluatorContext context) {
        node.xClass = runtime.ASTClass;

        Value lastValue = runtime.NIL;

        while (node.condition.visit(this, context).asBoolean()) {
            lastValue = node.body.visit(this, context.scope());
        }

        return lastValue;
    }

    @Override
    public Value visit(MethodDefinitionNode node, EvaluatorContext context) {
        node.xClass = runtime.ASTClass;

        Method method = new Method(node.name, (runtime, args) -> {
            List<MethodArgumentNode> formalArgs = node.arguments;

            // TODO: Should this be a scope gate?
            EvaluatorContext callContext = context.scope();
            callContext.setCurrentMethodName(node.name);

            callContext.defineLocal("self", args.get(0));

            // TODO: Check arity
            // TODO: Check types
            for (int i = 0; i < formalArgs.size(); i++) {
                callContext.defineLocal(formalArgs.get(i).name, args.get(i + 1));
            }

            return node.body.visit(this, callContext);
        });

        // TODO: Validate this is a class, not some other value
        Class selfClass = (Class)context.getLocal("Self");
        selfClass.defineMethod(method);

        return runtime.from(node.name);
    }

    @Override
    public Value visit(LambdaNode node, EvaluatorContext context) {
        node.xClass = runtime.ASTClass;

        // TODO: Move all object construction to Runtime
        return new LambdaValue(runtime.LambdaClass, (runtime, args) -> {
            List<MethodArgumentNode> formalArgs = node.arguments;

            EvaluatorContext callContext = context.scope();

            // TODO: Check arity
            // TODO: Check types
            for (int i = 0; i < formalArgs.size(); i++) {
                callContext.defineLocal(formalArgs.get(i).name, args.get(i + 1));
            }

            return node.body.visit(this, callContext);
        });
    }

    @Override
    public Value visit(MethodArgumentNode node, EvaluatorContext context) {
        throw new RuntimeException("This should not be called (visit MethodArgumentNode)");
    }

    @Override
    public Value visit(ClassDefinitionNode node, EvaluatorContext context) {
        node.xClass = runtime.ASTClass;

        if (runtime.getClass(node.name) != null) {
            throw new Evaluator.RuntimeError("Cannot redefine class " + node.name);
        }

        Class superclass = runtime.getClass(node.superclass);
        if (superclass == null) {
            throw new Evaluator.RuntimeError("Cannot inherit from undefined class " + node.superclass);
        }

        // TODO: This should be a scope gate, make a new context not descending from this one
        EvaluatorContext classContext = context.scope();

        Class klass = new Class(node.name, runtime.ClassClass, superclass);
        runtime.defineClass(klass);

        classContext.defineLocal("Self", klass);
        classContext.defineLocal("self", klass);

        return node.body.visit(this, classContext);
    }

    @Override
    public Value visit(CatchNode node, EvaluatorContext context) {
        throw new RuntimeException("This should not be called (visit CatchNode)");
    }
}
