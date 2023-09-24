package de.fh.semantic.err;

import de.fh.semantic.ComplexParserType;
import de.fh.semantic.closure.IClosure;

public class NoReturnSemanticException extends SemanticException {
    public NoReturnSemanticException(IClosure<String, ComplexParserType, Object> iClosure) {
        super(iClosure, "Methode hat einen Datentyp für die Rückgabe, eine Rückgabe des Werts fehlt jedoch.");
    }
}
