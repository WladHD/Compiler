package de.fh;

import de.fh.javacc.generated.ParseException;
import de.fh.javacc.generated.SimpleNode;
import de.fh.lexparser.LexParser;
import de.fh.semantic.SemanticAnalyzer;
import de.fh.translator.Translator;

public class Main {
    public static void main(String[] args) throws ParseException {
        Compiler<SimpleNode> compiler = new Compiler<>(
                new LexParser(),
                new SemanticAnalyzer(),
                new Translator(),
                true
        );

        int y = 4;
        int x = y++ -3;
        int z = y++ - -3;

        compiler.executePipelineFromResource("test.txt", null);
    }
}