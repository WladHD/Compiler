package de.fh.semantic.err;

import java.text.MessageFormat;

public class MethodNotDeclaredSemanticException extends SemanticException {
    public MethodNotDeclaredSemanticException(String varName) {
        super(3, MessageFormat.format("Methode {0} wurde noch nicht deklariert.", varName));
    }
}
