package de.fh.semantic.err;

import de.fh.semantic.ComplexParserType;
import de.fh.semantic.closure.IClosure;

public class NotYetSupportedSemanticException extends SemanticException {
    public NotYetSupportedSemanticException(IClosure<String, ComplexParserType, Object> closure, String errorMessage) {
        super(closure, errorMessage);
    }
}
