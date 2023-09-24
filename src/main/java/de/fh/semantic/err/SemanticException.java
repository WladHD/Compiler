package de.fh.semantic.err;

import de.fh.semantic.ComplexParserType;
import de.fh.semantic.closure.IClosure;

public abstract class SemanticException extends RuntimeException {

    private final IClosure<String, ComplexParserType, Object> iClosure;

    public SemanticException(IClosure<String, ComplexParserType, Object> iClosure, String message) {
        super(message);
        this.iClosure = iClosure;
    }

    public String getMessage() {
        return String.format("[%s] %s%nClosure: %s", getClass().getSimpleName(), super.getMessage(), iClosure == null ? "(Kein Closure angegeben)" : iClosure.getRecursiveClosureName());
    }


}
