package de.fh.semantic.err;

import java.text.MessageFormat;

public class UnreachableCodeSemanticException extends SemanticException {
    public UnreachableCodeSemanticException() {
        super(4, "Anweisungen nach einem Return sind nicht erreichbar.");
    }
}
