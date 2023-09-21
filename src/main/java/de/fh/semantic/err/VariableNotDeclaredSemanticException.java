package de.fh.semantic.err;

import java.text.MessageFormat;

public class VariableNotDeclaredSemanticException extends SemanticException {
    public VariableNotDeclaredSemanticException(String varName) {
        super(2, MessageFormat.format("Variable {0} wurde noch nicht deklariert, um einen Wert zuweisen zu können.", varName));
    }
}
