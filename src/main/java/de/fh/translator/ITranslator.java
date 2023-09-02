package de.fh.translator;

import de.fh.javacc.generated.Node;

public interface ITranslator<T extends Node> {

    public String getJavaCode(T rootNode);

    public byte[] getJavaByteCode(String javaCode);

}
