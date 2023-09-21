package de.fh.semantic.err;

public class NoReturnSemanticException extends SemanticException {
    public NoReturnSemanticException() {
        super(6, "Methode hat einen Datentyp für die Rückgabe, eine Rückgabe des Werts fehlt jedoch.");
    }
}
