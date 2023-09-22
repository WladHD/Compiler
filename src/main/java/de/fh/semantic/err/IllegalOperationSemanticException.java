package de.fh.semantic.err;

import de.fh.semantic.ComplexParserType;

import java.text.MessageFormat;

public class IllegalOperationSemanticException extends SemanticException {
    public IllegalOperationSemanticException(ComplexParserType cpt, ComplexParserType cpt2, String operator) {
        super(4, MessageFormat.format("Operation {0} {1} {2} kann nicht durchgeführt werden.", cpt != null ? cpt.toString() : "", operator, cpt2.toString()));
    }
}
