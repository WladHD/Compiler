package de.fh.semantic;

import de.fh.javacc.generated.SimpleNode;
import de.fh.semantic.err.SemanticException;
import de.fh.semantic.err.UnknownSemanticException;

public class SemanticAnalyzer implements ISemanticAnalyzer<SimpleNode> {

    @Override
    public void doSemanticCheck(SimpleNode rootNode) throws SemanticException {
        // throw new UnknownSemanticException();
    }

}
