package de.fh.semantic.err;

import de.fh.semantic.ComplexParserType;
import de.fh.semantic.closure.IClosure;

import java.text.MessageFormat;

public class StatementExpectedSemanticException extends SemanticException {
    public StatementExpectedSemanticException(IClosure<String, ComplexParserType, Object> iClosure, String varName) {
        super(iClosure, MessageFormat.format("Es wurde ein Statement (Deklaration, Initialisation, Methodenaufruf) in {0} erwartet.", varName));
    }
}
