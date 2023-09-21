package de.fh.semantic.err;

import de.fh.javacc.generated.SimpleNode;

import java.text.MessageFormat;

public class InternClosureCastingSemanticException extends SemanticException {
    public InternClosureCastingSemanticException() {
        super(-1, "Unerwarteter Weise ist ein interner Fehler beim Closure Casting aufgetreten ...");
    }
}
