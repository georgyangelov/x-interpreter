package net.gangelov.x.ast.nodes;

import com.sun.istack.internal.Nullable;
import net.gangelov.x.ast.ASTNode;
import net.gangelov.x.ast.AbstractVisitor;

public class CatchNode extends ASTNode {
    public final String klass, name;
    public final BlockNode body;

    public CatchNode(String klass, @Nullable String name, BlockNode body) {
        super();

        this.klass = klass;
        this.name = name;
        this.body = body;
    }

    @Override
    public <T, C> T visit(AbstractVisitor<T, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
