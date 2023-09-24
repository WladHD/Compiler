package de.fh.semantic.err;

public class UnknownSemanticException extends SemanticException {
    public UnknownSemanticException(String errorMessage) {
        super(null, (errorMessage != null) ? errorMessage : "Something went really wrong");
    }
}
