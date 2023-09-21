package de.fh;

import de.fh.javacc.generated.ParseException;
import de.fh.javacc.generated.SimpleNode;
import de.fh.lexparser.LexParser;
import de.fh.semantic.SemanticAnalyzer;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private final static boolean standalone = false;
    public static Logger logger;

    public static void main(String[] args) throws ParseException {
        logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        logger.setLevel(Level.ALL);

        Compiler<SimpleNode> compiler = new Compiler<>(
                new LexParser(),
                new SemanticAnalyzer(),
                null,
                true
        );

        String y = "sdf" + 1;

        if (standalone && args.length == 0) {
            System.out.println("<Pfad zu Source Datei> [Optional: Pfad zu Output Datei]");
            return;
        }

        String source = "cleanTestSmall.txt";
        String output = "./src/test/java/TranslatorTemplate.java";

        if (args.length > 0) {
            source = args[0];
            output = args.length > 1 ? args[1] : null;
        }

        int[] arr = {1, 2, 3, 4};
        int x = arr[arr[2] = 1] = 34;

        compiler.executePipelineFromResource(source, output);
    }
}