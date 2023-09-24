package de.fh.semantic.err;

import de.fh.semantic.ComplexParserType;
import de.fh.semantic.closure.IClosure;

import java.text.MessageFormat;

public class VariableNotDeclaredSemanticException extends SemanticException {
    public VariableNotDeclaredSemanticException(IClosure<String, ComplexParserType, Object> iClosure, String varName) {
        super(iClosure, MessageFormat.format("Variable {0} wurde noch nicht deklariert, um einen Wert zuweisen zu können.", varName));
    }
}
