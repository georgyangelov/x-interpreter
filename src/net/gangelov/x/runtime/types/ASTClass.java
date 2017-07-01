package net.gangelov.x.runtime.types;

import net.gangelov.x.ast.ASTNode;
import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.StringValue;

import java.util.List;

public class ASTClass extends Class {
    public ASTClass() {
        super("AST");

        defineMethod(new Method("inspect", (List<Value> args) -> {
            ASTNode self = (ASTNode)args.get(0);

            return new StringValue(self.inspect());
        }));
    }
}
