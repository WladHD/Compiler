package de.fh.translator;

import de.fh.javacc.generated.Node;

public interface ITranslator<T extends Node> {

    String getJavaCode(T rootNode);

    byte[] getJavaByteCode(String javaCode);

}
