package de.fh.semantic.err;

import java.text.MessageFormat;

public class VariableDeclarationNotAllowedSemanticException extends SemanticException {
    public VariableDeclarationNotAllowedSemanticException(String varName, String stmtName) {
        super(1, MessageFormat.format("Variablendeklaration von {0} ist in {1} nicht erlaubt.", varName, stmtName));
    }
}
