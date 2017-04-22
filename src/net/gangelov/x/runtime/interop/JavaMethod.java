package net.gangelov.x.runtime.interop;

import net.gangelov.x.runtime.base.Method;
import net.gangelov.x.runtime.base.Value;

import java.util.List;

public class JavaMethod extends Method {
    public interface JavaMethodCallee {
        Value call(Value self, List<Value> args);
    }

    private JavaMethodCallee callee;

    public JavaMethod(JavaMethodCallee callee) {
        this.callee = callee;
    }

    @Override
    public Value call(Value self, List<Value> args) {
        return callee.call(self, args);
    }
}
