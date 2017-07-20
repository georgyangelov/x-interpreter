package net.gangelov.x.evaluator;

import net.gangelov.x.runtime.Value;

public class XErrorException extends Evaluator.RuntimeError {
    public final Value error;

    public XErrorException(Value error) {
        super(error.inspect());

        this.error = error;
    }
}
