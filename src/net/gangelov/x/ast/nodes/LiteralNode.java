package net.gangelov.x.ast.nodes;

import net.gangelov.x.ast.ASTNode;

public abstract class LiteralNode extends ASTNode {
    public String str;

    public LiteralNode() {
        super();
    }

    public LiteralNode(String str) {
        this.str = str;
    }
}
