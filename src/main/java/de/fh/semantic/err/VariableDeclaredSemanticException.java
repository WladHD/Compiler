package de.fh.semantic.err;

import de.fh.semantic.ComplexParserType;
import de.fh.semantic.closure.IClosure;

import java.text.MessageFormat;

public class VariableDeclaredSemanticException extends SemanticException {
    public VariableDeclaredSemanticException(IClosure<String, ComplexParserType, Object> iClosure, String varName) {
        super(iClosure, MessageFormat.format("Variable {0} wurde bereits deklariert.", varName));
    }
}
