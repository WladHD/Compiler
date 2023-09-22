package de.fh.semantic.err;

import de.fh.semantic.ComplexParserType;

import java.text.MessageFormat;
import java.util.ArrayList;

public class ReturnTypeMismatchSemanticException extends SemanticException {
    public ReturnTypeMismatchSemanticException(String methodName, ComplexParserType expected, ComplexParserType given) {
        super(6, MessageFormat.format("Für die Methode {0} wurde {1} als Rückgabewert erwartet, jedoch {2} erhalten.", methodName, expected, given));
    }
}
