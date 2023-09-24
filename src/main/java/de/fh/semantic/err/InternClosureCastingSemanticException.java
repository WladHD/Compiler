package de.fh.semantic.err;

import de.fh.javacc.generated.SimpleNode;
import de.fh.semantic.ComplexParserType;
import de.fh.semantic.closure.IClosure;

import java.text.MessageFormat;

public class InternClosureCastingSemanticException extends SemanticException {
    public InternClosureCastingSemanticException() {
        super(null, "Unerwarteter Weise ist ein interner Fehler beim Closure Casting aufgetreten ...");
    }
}
