package de.fh;

import de.fh.javacc.generated.ParseException;
import de.fh.javacc.generated.SimpleNode;
import de.fh.lexparser.LexParser;
import de.fh.semantic.SemanticAnalyzer;
import de.fh.utils.CustomFormatter;

import java.awt.*;
import java.lang.reflect.ParameterizedType;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private final static boolean standalone = false;
    public static Logger logger;

    public static void main(String[] args) throws ParseException {
        CustomFormatter cs = new CustomFormatter();
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(cs);
        logger = Logger.getLogger(Main.class.getName());
        logger.setLevel(Level.ALL);

        for (Handler iHandler : logger.getParent().getHandlers()) {
            logger.getParent().removeHandler(iHandler);
        }

        logger.addHandler(consoleHandler);

        Compiler<SimpleNode> compiler = new Compiler<>(
                new LexParser(),
                new SemanticAnalyzer(),
                null,
                true
        );

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

        compiler.executePipelineFromResource(source, output);
    }
}