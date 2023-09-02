package de.fh.semantic.err;

public abstract class SemanticException extends Exception {

    private final int errorCode;

    public SemanticException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return String.format("[%s #%d] %s", getClass().getSimpleName(), errorCode, super.getMessage());
    }


}
