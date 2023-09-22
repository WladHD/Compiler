package de.fh.semantic.err;

import de.fh.javacc.generated.SimpleNode;

import java.text.MessageFormat;

public class InternClosureSemanticException extends SemanticException {
    public InternClosureSemanticException(SimpleNode causeNode) {
        super(-1, MessageFormat.format("Unerwarteter Weise gab es bei {} kein Closure ...", causeNode.toString()));
    }
}