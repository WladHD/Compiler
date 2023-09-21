package de.fh.semantic.err;

import de.fh.semantic.ComplexParserType;

import java.text.MessageFormat;

public class ExpectedTypeMissmatchSemanticException extends SemanticException {
    public ExpectedTypeMissmatchSemanticException(ComplexParserType expected, Object gotten) {
        super(4, MessageFormat.format("Es wurde der Typ {0} erwartet, jedoch wurde {1} erhalten.", expected.toString(), gotten.toString()));
    }
}
