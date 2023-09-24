package de.fh.semantic.err;

import java.text.MessageFormat;

public class MainMethodWithArgsSemanticException extends SemanticException {
    public MainMethodWithArgsSemanticException(int args) {
        super(4, MessageFormat.format("Die main() Methode darf keine Argumente enthalten, hat jedoch {0}.", args));
    }
}
