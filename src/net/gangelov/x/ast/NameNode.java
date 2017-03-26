package net.gangelov.x.ast;

public class NameNode extends ASTNode {
    public final String name;

    public NameNode(String name) {
        super();
        this.name = name;
    }

    @Override
    public void visit(Visitor visitor) {
        visitor.visit(this);
    }
}
