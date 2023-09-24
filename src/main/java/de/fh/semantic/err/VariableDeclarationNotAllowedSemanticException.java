package de.fh.semantic.err;

import de.fh.semantic.ComplexParserType;
import de.fh.semantic.closure.IClosure;

import java.text.MessageFormat;

public class VariableDeclarationNotAllowedSemanticException extends SemanticException {
    public VariableDeclarationNotAllowedSemanticException(IClosure<String, ComplexParserType, Object> iClosure, String varName, String stmtName) {
        super(iClosure, MessageFormat.format("Variablendeklaration von {0} ist in {1} nicht erlaubt.", varName, stmtName));
    }
}
