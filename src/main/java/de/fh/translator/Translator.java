package de.fh.translator;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import de.fh.javacc.generated.SimpleNode;
import de.fh.translator.visitor.TranslateVisitor;

public class Translator implements ITranslator<SimpleNode> {
    @Override
    public String getJavaCode(SimpleNode rootNode) {
        TranslateVisitor tv = new TranslateVisitor();

        String rawResult = (String) tv.visit(rootNode, rootNode.jjtGetValue());

        try {
            return new Formatter().formatSource(rawResult);
        } catch (FormatterException e) {
            return rawResult;
        }
    }

    @Override
    public byte[] getJavaByteCode(String javaCode) {
        return new byte[0];
    }
}
