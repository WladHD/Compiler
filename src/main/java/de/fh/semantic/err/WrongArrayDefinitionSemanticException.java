package de.fh.semantic.err;

import de.fh.semantic.ComplexParserType;
import de.fh.semantic.closure.IClosure;

public class WrongArrayDefinitionSemanticException extends SemanticException {
    public WrongArrayDefinitionSemanticException(IClosure<String, ComplexParserType, Object> iClosure) {
        super(iClosure, "Eine Array wird mit '{'...'}' definiert, wobei Set und Map mit [...] definiert werden.");
    }
}
