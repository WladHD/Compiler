package de.fh.semantic.err;

import java.text.MessageFormat;

public class MethodDeclaredSemanticException extends SemanticException {
    public MethodDeclaredSemanticException(String varName) {
        super(3, MessageFormat.format("Methode {0} wurde bereits deklariert.", varName));
    }
}
