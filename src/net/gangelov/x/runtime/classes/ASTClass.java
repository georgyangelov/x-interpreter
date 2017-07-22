package net.gangelov.x.runtime.classes;

import net.gangelov.x.ast.ASTNode;
import net.gangelov.x.runtime.Runtime;
import net.gangelov.x.runtime.Value;
import net.gangelov.x.runtime.base.Class;
import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.builtins.StringValue;

import java.util.List;

public class ASTClass extends Class {
    public ASTClass(Runtime r) {
        super("AST", r, r.ObjectClass);

        defineMethod(new Method("inspect", 0, 0, (runtime, args) -> {
            ASTNode self = (ASTNode)args.get(0);

            return runtime.from(self.inspect());
        }));
    }
}
