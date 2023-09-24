package de.fh.semantic.err;

import de.fh.semantic.ComplexParserType;
import de.fh.semantic.closure.IClosure;

import java.text.MessageFormat;

public class WrongArrayDefinitionSemanticException extends SemanticException {
    public WrongArrayDefinitionSemanticException(IClosure<String, ComplexParserType, Object> iClosure, String msg) {
        super(iClosure, msg);
    }
}
