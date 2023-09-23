package de.fh.translator;

import de.fh.javacc.generated.*;
import de.fh.semantic.ComplexParserType;

public class Translator implements ITranslator<SimpleNode> {

    private final StringBuilder sb;

    public Translator() {
        sb = new StringBuilder();
    }

    @Override
    public String getJavaCode() {
        return sb.toString();
    }

    @Override
    public StringBuilder getStringBuilder() {
        return sb;
    }

    @Override
    public void add(Object s) {
        sb.append(s.toString());
    }
}