package de.fh;

import de.fh.javacc.generated.ParseException;
import de.fh.javacc.generated.SimpleNode;
import de.fh.lexparser.LexParser;
import de.fh.semantic.SemanticAnalyzer;
import de.fh.translator.Translator;
import de.fh.translator.sandbox.TranslatorTemplate;

public class Main {
    private final static boolean standalone = false;

    public static void main(String[] args) throws ParseException {
        Compiler<SimpleNode> compiler = new Compiler<>(
                new LexParser(),
                null,
                new Translator(),
                true
        );

        if(standalone && args.length == 0) {
            System.out.println("<Pfad zu Source Datei> [Optional: Pfad zu Output Datei]");
            return;
        }

        String source = "arrays.txt";
        String output = "./src/test/java/TranslatorTemplate.java";

        if(args.length > 0) {
            source = args[0];
            output = args.length > 1 ? args[1] : null;
        }

        compiler.executePipelineFromResource(source, output);
    }
}