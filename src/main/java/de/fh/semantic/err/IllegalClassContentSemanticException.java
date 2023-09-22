package de.fh.semantic.err;

public class IllegalClassContentSemanticException extends SemanticException {
    public IllegalClassContentSemanticException() {
        super(-1, "Eine Klasse darf nur Variablendeklarationen, Initialisationen, Blöcke und Methoden enthalten.");
    }
}
