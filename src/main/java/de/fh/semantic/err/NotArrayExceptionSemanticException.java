package de.fh.semantic.err;

import de.fh.semantic.ComplexParserType;
import de.fh.semantic.closure.IClosure;

import java.text.MessageFormat;
import java.util.ArrayList;

public class NotArrayExceptionSemanticException extends SemanticException {
    public NotArrayExceptionSemanticException(IClosure<String, ComplexParserType, Object> iClosure, Object value, ComplexParserType type) {
        super(iClosure, MessageFormat.format("Ein Arrayzugriff ist auf {0} der Typs {1} nicht möglich, da es keine Array ist.", value, type));
    }
}
