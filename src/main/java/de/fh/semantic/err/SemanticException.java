package de.fh.semantic.err;

public abstract class SemanticException extends RuntimeException {

    public SemanticException(int errorCode, String message) {
        super(message);
    }

    public String getMessage() {
        return String.format("[%s] %s", getClass().getSimpleName(), super.getMessage());
    }


}
