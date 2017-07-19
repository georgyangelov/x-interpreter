package net.gangelov.x.resolver;

import net.gangelov.x.ast.ASTNode;
import net.gangelov.x.ast.AbstractVisitor;
import net.gangelov.x.ast.Visitor;
import net.gangelov.x.ast.nodes.*;
import net.gangelov.x.runtime.base.Class;

import java.util.List;
import java.util.stream.Collectors;

public class ResolverBuilder extends AbstractVisitor<Resolver.Node, ResolverScope> {
    private final Resolver resolver;
    private final ResolverScope rootScope;

    public ResolverBuilder(Resolver resolver, ResolverScope rootScope) {
        this.resolver = resolver;
        this.rootScope = rootScope;
    }

    public void build(ASTNode node) {
        node.visit(this, rootScope);
    }

    @Override
    public Resolver.Node visit(NumberLiteralNode node, ResolverScope context) {
        Class intType = context.getClass("Int");

        return resolver.defineConstant(intType);
    }

    @Override
    public Resolver.Node visit(StringLiteralNode node, ResolverScope context) {
        Class stringType = context.getClass("String");

        return resolver.defineConstant(stringType);
    }

    @Override
    public Resolver.Node visit(NameNode node, ResolverScope context) {
        Resolver.VariableNode var = context.getVariable(node.name);

        if (var == null) {
            // TODO: Raise error: variable cannot be used before it is defined
        }

        return var;
    }

    @Override
    public Resolver.Node visit(AssignmentNode node, ResolverScope context) {
        Resolver.Node value = node.value.visit(this, context);
        Resolver.VariableNode var = context.getVariable(node.name);

        if (var == null) {
            var = resolver.defineVariable(node.name);
            context.defineVariable(var);
        }

        resolver.assign(value, var);

        return value;
    }

    @Override
    public Resolver.Node visit(MethodCallNode node, ResolverScope context) {
        List<Resolver.Node> arguments = node.arguments.stream()
                .map(n -> n.visit(this, context))
                .collect(Collectors.toList());

        return resolver.methodCall(node.name, arguments);
    }

    @Override
    public Resolver.Node visit(BranchNode node, ResolverScope context) {
        node.condition.visit(this, context);
        node.true_branch.visit(this, context);
        node.false_branch.visit(this, context);

        return null;
    }

    @Override
    public Resolver.Node visit(BlockNode node, ResolverScope context) {
        for (ASTNode n : node.nodes) {
            n.visit(this, context);
        }

        return null;
    }

    @Override
    public Resolver.Node visit(WhileNode node, ResolverScope context) {
        node.condition.visit(this, context);
        node.body.visit(this, context);

        return null;
    }

    @Override
    public Resolver.Node visit(MethodDefinitionNode node, ResolverScope context) {
        for (ASTNode n : node.arguments) {
            n.visit(this, context);
        }

        node.body.visit(this, context);

        return null;
    }

    @Override
    public Resolver.Node visit(MethodArgumentNode node, ResolverScope context) {
        return null;
    }
}
