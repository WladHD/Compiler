package de.fh.translator;

import de.fh.javacc.generated.SimpleNode;
import de.fh.utils.ClassLoaderReader;

import java.text.MessageFormat;

public class Translator implements ITranslator<SimpleNode> {

    private final StringBuilder sb;

    public Translator() {
        sb = new StringBuilder();
    }

    @Override
    public String getJavaCode() {
        String generatedCode = sb.toString();
        String templateResourceName = "TranslatorTemplate.java";

        String template = ClassLoaderReader.getResourceFileAsString(templateResourceName);

        if(template == null)
            throw new RuntimeException(MessageFormat.format("Konnte TranslatorTemplate.java nicht aus {0} laden.", templateResourceName));

        return template.replace("/* PLACEHOLDER */", generatedCode);
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