package de.fh.semantic.err;

import de.fh.semantic.ComplexParserType;
import de.fh.semantic.closure.IClosure;

import java.text.MessageFormat;

public class IllegalOperationSemanticException extends SemanticException {
    public IllegalOperationSemanticException(IClosure<String, ComplexParserType, Object> iClosure, ComplexParserType cpt, ComplexParserType cpt2, String operator) {
        super(iClosure, MessageFormat.format("Operation {0} {1} {2} kann nicht durchgeführt werden.", cpt != null ? cpt.toString() : "", operator, cpt2.toString()));
    }
}
