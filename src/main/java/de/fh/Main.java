package de.fh;

import de.fh.javacc.generated.ParseException;
import de.fh.javacc.generated.SimpleNode;
import de.fh.lexparser.LexParser;

public class Main {
    private final static boolean standalone = false;

    public static void main(String[] args) throws ParseException {
        Compiler<SimpleNode> compiler = new Compiler<>(
                new LexParser(),
                null,
                null,
                true
        );

        if (standalone && args.length == 0) {
            System.out.println("<Pfad zu Source Datei> [Optional: Pfad zu Output Datei]");
            return;
        }

        String source = "cleanTest.txt";
        String output = "./src/test/java/TranslatorTemplate.java";

        if (args.length > 0) {
            source = args[0];
            output = args.length > 1 ? args[1] : null;
        }

        int[] arr = {1,2,3,4};
        int x = arr[arr[2] = 1] = 34;

        compiler.executePipelineFromResource(source, output);
    }
}