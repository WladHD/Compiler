package de.fh.semantic.err;

import de.fh.semantic.ComplexParserType;
import de.fh.semantic.closure.IClosure;

public class UnknownSemanticException extends SemanticException {
    public UnknownSemanticException(String errorMessage) {
        super(null, errorMessage);
    }

    public UnknownSemanticException(IClosure<String, ComplexParserType, Object> closure, String errorMessage) {
        super(closure, (errorMessage != null) ? errorMessage : "Something went really wrong");
    }
}
