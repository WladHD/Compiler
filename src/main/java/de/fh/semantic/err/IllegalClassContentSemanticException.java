package de.fh.semantic.err;

import de.fh.semantic.ComplexParserType;
import de.fh.semantic.closure.IClosure;

public class IllegalClassContentSemanticException extends SemanticException {
    public IllegalClassContentSemanticException(IClosure<String, ComplexParserType, Object> iClosure) {
        super(iClosure, "Eine Klasse darf nur Variablendeklarationen, Blöcke und Methoden enthalten.");
    }
}
