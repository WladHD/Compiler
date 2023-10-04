package de.fh.semantic.err;

import de.fh.semantic.ComplexParserType;
import de.fh.semantic.closure.IClosure;

public class IllegalInitializerSemanticException extends SemanticException {
    public IllegalInitializerSemanticException(IClosure<String, ComplexParserType, Object> closure) {
        super(closure, "Eine Methode darf nicht auf der linken Seite einer Zuweisung stehen.");
    }
}
