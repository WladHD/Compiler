package de.fh.semantic.err;

import de.fh.semantic.ComplexParserType;
import de.fh.semantic.closure.IClosure;

import java.text.MessageFormat;

public class UnreachableCodeSemanticException extends SemanticException {
    public UnreachableCodeSemanticException(IClosure<String, ComplexParserType, Object> iClosure) {
        super(iClosure, "Anweisungen nach einem Return sind nicht erreichbar.");
    }
}
