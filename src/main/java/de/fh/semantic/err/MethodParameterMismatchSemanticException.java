package de.fh.semantic.err;

import de.fh.semantic.ComplexParserType;
import de.fh.semantic.closure.IClosure;

import java.text.MessageFormat;
import java.util.ArrayList;

public class MethodParameterMismatchSemanticException extends SemanticException {
    public MethodParameterMismatchSemanticException(IClosure<String, ComplexParserType, Object> iClosure, String methodName, ArrayList<ComplexParserType> expected, ArrayList<ComplexParserType> given) {
        super(iClosure, MessageFormat.format("Für die Methode {0} wurde {1} Parameter erwartet, jedoch {2} erhalten.", methodName, expected, given));
    }
}
