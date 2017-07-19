package net.gangelov.x.ast;

import net.gangelov.x.ast.nodes.*;

public abstract class AbstractVisitor<T, C> {
    abstract public T visit(LiteralNode node, C context);
    abstract public T visit(NameNode node, C context);
    abstract public T visit(AssignmentNode node, C context);
    abstract public T visit(MethodCallNode node, C context);
    abstract public T visit(BranchNode node, C context);
    abstract public T visit(BlockNode node, C context);
    abstract public T visit(WhileNode node, C context);
    abstract public T visit(MethodDefinitionNode methodDefinitionNode, C context);
    abstract public T visit(MethodArgumentNode methodDefinitionNode, C context);
}
