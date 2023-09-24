package de.fh.semantic.err;

import de.fh.javacc.generated.SimpleNode;
import de.fh.semantic.ComplexParserType;
import de.fh.semantic.closure.IClosure;

import java.text.MessageFormat;

public class ExpectedTypeMissmatchSemanticException extends SemanticException {
    public ExpectedTypeMissmatchSemanticException(IClosure<String, ComplexParserType, Object> iClosure, ComplexParserType expected, Object gotten) {
        super(iClosure, MessageFormat.format("Es wurde der Typ {0} erwartet, jedoch wurde {1} erhalten.", expected.toString(), gotten));
    }
}
