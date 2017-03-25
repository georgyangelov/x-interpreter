package net.gangelov.x.ast;

public abstract class ASTNode {
    abstract public <T> void visit(Visitor<T> visitor);
}
