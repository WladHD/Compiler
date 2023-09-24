package de.fh.semantic.err;

import de.fh.semantic.ComplexParserType;
import de.fh.semantic.closure.IClosure;

import java.text.MessageFormat;
import java.util.ArrayList;

public class ReturnTypeMismatchSemanticException extends SemanticException {
    public ReturnTypeMismatchSemanticException(IClosure<String, ComplexParserType, Object> iClosure, String methodName, ComplexParserType expected, ComplexParserType given) {
        super(iClosure, MessageFormat.format("Für die Methode {0} wurde {1} als Rückgabewert erwartet, jedoch {2} erhalten.", methodName, expected, given));
    }
}
