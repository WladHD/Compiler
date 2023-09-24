package de.fh.semantic.err;

import de.fh.semantic.ComplexParserType;
import de.fh.semantic.closure.IClosure;

import java.text.MessageFormat;

public class MethodDeclaredSemanticException extends SemanticException {
    public MethodDeclaredSemanticException(IClosure<String, ComplexParserType, Object> iClosure, String varName) {
        super(iClosure, MessageFormat.format("Methode {0} wurde bereits deklariert.", varName));
    }
}
