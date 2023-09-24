package de.fh.semantic.err;

import de.fh.semantic.ComplexParserType;
import de.fh.semantic.closure.IClosure;

import java.text.MessageFormat;

public class VariableNotInitializedSemanticException extends SemanticException {
    public VariableNotInitializedSemanticException(IClosure<String, ComplexParserType, Object> iClosure, String varName) {
        super(iClosure, MessageFormat.format("Variable {0} wurde noch nicht initialisiert, um einen Wert zuweisen zu können.", varName));
    }
}
