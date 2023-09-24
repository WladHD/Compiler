package de.fh.semantic.err;

import de.fh.semantic.ComplexParserType;
import de.fh.semantic.closure.IClosure;

import java.text.MessageFormat;

public class MainMethodWithArgsSemanticException extends SemanticException {
    public MainMethodWithArgsSemanticException(IClosure<String, ComplexParserType, Object> iClosure, int args) {
        super(iClosure, MessageFormat.format("Die main() Methode darf keine Argumente enthalten, hat jedoch {0}.", args));
    }
}
