package de.fh.translator;

import de.fh.javacc.generated.SimpleNode;

public class Translator implements ITranslator<SimpleNode> {
    @Override
    public String getJavaCode(SimpleNode rootNode) {
        return null;
    }

    @Override
    public byte[] getJavaByteCode(String javaCode) {
        return new byte[0];
    }
}
