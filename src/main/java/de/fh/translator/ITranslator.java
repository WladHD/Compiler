package de.fh.translator;

import de.fh.javacc.generated.Node;

public interface ITranslator<T extends Node> {

    String getJavaCode();

    StringBuilder getStringBuilder();

    void add(Object s);

}
