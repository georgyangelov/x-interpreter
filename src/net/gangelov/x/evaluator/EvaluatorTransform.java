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

    @Override
    public Value visit(LiteralNode node, EvaluatorContext context) {
        // TODO: Move these to the runtime
        node.xClass = runtime.ASTClass;

        switch (node.type) {
            case Nil:
                return runtime.NIL;
            case Bool:
                return runtime.wrap(node.str.equals("true"));
            case Int:
                return runtime.wrap(Integer.parseInt(node.str));
            case String:
                return runtime.wrap(node.str);
            case Float:
                return runtime.wrap(Double.parseDouble(node.str));
        }

        return null;
    }

    @Override
    public Value visit(NameNode node, EvaluatorContext context) {
        node.xClass = runtime.ASTClass;

        Value value = context.getLocal(node.name);

        if (node.name.startsWith("@") && value == null) {
            throw new Evaluator.RuntimeError("Undefined instance variable " + node.name);
        }

        if (value == null) {
            Class klass = runtime.getClass(node.name);

            if (klass != null) {
                return klass;
            }

            // Maybe this is a method call without arguments?
            return new MethodCallNode(node.name, false, new NameNode("self")).visit(this, context);
        }

        return value;
    }

    @Override
    public Value visit(AssignmentNode node, EvaluatorContext context) {
        node.xClass = runtime.ASTClass;

        Value value = node.value.visit(this, context);

        context.assignLocal(node.name, value);

        return value;
    }

    @Override
    public Value visit(MethodCallNode node, EvaluatorContext context) {
        node.xClass = runtime.ASTClass;

        if (node.name.equals("and")) {
            Value first = node.arguments.get(0).visit(this, context);

            if (!first.asBoolean()) {
                return first;
            }

            return node.arguments.get(1).visit(this, context);
        } else if (node.name.equals("or")) {
            Value first = node.arguments.get(0).visit(this, context);

            if (first.asBoolean()) {
                return first;
            }

            return node.arguments.get(1).visit(this, context);
        }

        List<Value> arguments = node.arguments.stream()
                .map(argument -> argument.visit(this, context))
                .collect(Collectors.toList());

        Value self = arguments.get(0);

        if (node.name.equals("super")) {
            String methodName = context.getCurrentMethodName();

            if (methodName == null) {
                throw new Evaluator.RuntimeError("Cannot call super outside of a method");
            }

            Class superclass = self.getXClass().getSuperClass();
            if (superclass == null) {
                throw new Evaluator.RuntimeError(
                        "Cannot call super because " + self.getXClass().name + " does not have a superclass"
                );
            }

            Method method = superclass.getInstanceMethod(methodName);
            if (method == null) {
                throw new Evaluator.RuntimeError(
                        "No method " + methodName + " on class " + superclass.name
                );
            }

            return method.call(runtime, arguments);
        }

        Method method;

        // TODO: Simplify!
        // local_variable(args)
        if (node.mayBeVariableCall) {
            Value lambda = context.getLocal(node.name);

            if (lambda != null) {
                method = lambda.getMethod("call");

                if (method != null) {
                    self = context.getLocal(node.name);
                    arguments.set(0, self);
                } else {
                    method = self.getMethod(node.name);
                }
            } else {
                method = self.getMethod(node.name);
            }
        } else {
            method = self.getMethod(node.name);
        }

        if (method == null) {
            throw new Evaluator.RuntimeError(
                    "No method " + node.name + " on class " + self.getXClass().name
            );
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

        int requiredArgs = node.arguments.size();

        Method method = new Method(node.name, requiredArgs, 0, (runtime, args) -> {
            List<MethodArgumentNode> formalArgs = node.arguments;

            // TODO: Should this be a scope gate?
            EvaluatorContext callContext = context.scope();
            callContext.setCurrentMethodName(node.name);

            callContext.defineLocal("self", args.get(0));

            // TODO: Check types
            for (int i = 0; i < formalArgs.size(); i++) {
                callContext.defineLocal(formalArgs.get(i).name, args.get(i + 1));
            }

            return node.body.visit(this, callContext);
        });

        // TODO: Validate this is a class, not some other value
        Class selfClass = (Class)context.getLocal("Self");
        selfClass.defineMethod(method);

        return runtime.wrap(node.name);
    }

    @Override
    public Value visit(LambdaNode node, EvaluatorContext context) {
        node.xClass = runtime.ASTClass;

        int requiredArgs = node.arguments.size();

        // TODO: Move all object construction to Runtime
        return new LambdaValue(runtime.LambdaClass, new Method("<lambda>", requiredArgs, 0,
                (runtime, args) -> {
            List<MethodArgumentNode> formalArgs = node.arguments;
            LambdaValue lambda = (LambdaValue)args.get(0);

            EvaluatorContext callContext = context.scope();
            callContext.defineLocal("self", lambda.self);
            callContext.defineLocal("Self", lambda.Self);

            // TODO: Check types
            for (int i = 0; i < formalArgs.size(); i++) {
                callContext.defineLocal(formalArgs.get(i).name, args.get(i + 1));
            }

            return node.body.visit(this, callContext);
        }), context.getLocal("self"), context.getLocal("Self"));
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

        if (!superclass.canBeInherited()) {
            throw new Evaluator.RuntimeError("Cannot inherit from built-in class " + superclass.name);
        }

        Class klass = new Class(node.name, runtime, superclass);
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
