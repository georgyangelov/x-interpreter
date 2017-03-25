package net.gangelov.x.ast;

public abstract class LiteralNode extends ASTNode {
    public String str;

    public LiteralNode(String str) {
        this.str = str;
    }
}
