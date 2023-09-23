package de.fh.semantic;

import de.fh.javacc.generated.Node;
import de.fh.javacc.generated.SimpleNode;
import de.fh.semantic.err.SemanticException;
import de.fh.translator.ITranslator;

public interface ISemanticAnalyzer<T extends Node> {

    void doSemanticCheck(T rootNode, ITranslator<T> translator) throws SemanticException;

}
