package de.fh.semantic.err;

public class UnknownSemanticException extends SemanticException {
    public UnknownSemanticException() {
        super(42, "Something went really wrong");
    }
}
