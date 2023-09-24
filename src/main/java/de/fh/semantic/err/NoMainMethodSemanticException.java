package de.fh.semantic.err;

public class NoMainMethodSemanticException extends SemanticException {
    public NoMainMethodSemanticException() {
        super(4, "Programm verfügt über keine main() Methode.");
    }
}
