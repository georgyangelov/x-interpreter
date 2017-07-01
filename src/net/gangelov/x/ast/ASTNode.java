package net.gangelov.x.ast;

import net.gangelov.x.debug.ASTInspector;
import net.gangelov.x.runtime.Value;

public abstract class ASTNode extends Value {
    abstract public <T, C> T visit(AbstractVisitor<T, C> visitor, C context);

    @Override
    public String getClassName() {
        return "AST";
    }

    public String inspect() {
        return ASTInspector.inspect(this);
    }
}
