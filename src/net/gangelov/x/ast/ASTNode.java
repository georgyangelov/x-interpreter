package net.gangelov.x.ast;

public abstract class ASTNode {
    abstract public void visit(Visitor visitor);
}
