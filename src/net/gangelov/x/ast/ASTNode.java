package net.gangelov.x.ast;

public abstract class ASTNode {
    abstract public <T, C> T visit(AbstractVisitor<T, C> visitor, C context);
}
