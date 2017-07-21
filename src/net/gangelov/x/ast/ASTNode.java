package net.gangelov.x.ast;

import net.gangelov.x.debug.ASTInspector;
import net.gangelov.x.runtime.Runtime;
import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;

public abstract class ASTNode extends Value {
    public Class xClass;

    abstract public <T, C> T visit(AbstractVisitor<T, C> visitor, C context);

    @Override
    public Class getXClass() {
        return xClass;
    }

    public String inspect() {
        return ASTInspector.inspect(this);
    }
}
