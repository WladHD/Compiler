package de.fh.semantic;

import de.fh.javacc.generated.Node;
import de.fh.semantic.err.SemanticException;

public interface ISemanticAnalyzer<T extends Node> {

    void doSemanticCheck(T rootNode) throws SemanticException;

}
