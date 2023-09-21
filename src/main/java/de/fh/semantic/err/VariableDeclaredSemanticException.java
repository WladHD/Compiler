package de.fh.semantic.err;

import java.text.MessageFormat;

public class VariableDeclaredSemanticException extends SemanticException {
    public VariableDeclaredSemanticException(String varName) {
        super(1, MessageFormat.format("Variable {0} wurde bereits deklariert.", varName));
    }
}
