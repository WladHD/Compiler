package de.fh.semantic.err;

import de.fh.semantic.ComplexParserType;
import de.fh.semantic.closure.IClosure;

public class NoMainMethodSemanticException extends SemanticException {
    public NoMainMethodSemanticException(IClosure<String, ComplexParserType, Object> iClosure) {
        super(iClosure, "Programm verfügt über keine main() Methode.");
    }
}
