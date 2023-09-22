package de.fh.semantic.err;

import java.text.MessageFormat;

public class StatementExpectedSemanticException extends SemanticException {
    public StatementExpectedSemanticException(String varName) {
        super(1, MessageFormat.format("Es wurde ein Statement (Deklaration, Initialisation, Methodenaufruf) in {0} erwartet.", varName));
    }
}
