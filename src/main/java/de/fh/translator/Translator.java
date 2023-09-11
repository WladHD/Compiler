package de.fh.translator;

import de.fh.javacc.generated.SimpleNode;
import de.fh.translator.visitor.TranslateVisitor;

public class Translator implements ITranslator<SimpleNode> {
    @Override
    public String getJavaCode(SimpleNode rootNode) {
        TranslateVisitor tv = new TranslateVisitor();

        return (String) tv.visit(rootNode, rootNode.jjtGetValue());
    }

    @Override
    public byte[] getJavaByteCode(String javaCode) {
        return new byte[0];
    }
}
