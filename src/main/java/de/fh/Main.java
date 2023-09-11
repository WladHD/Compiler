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

        int x = 5;
        int y;
        int z = y = x++ + x / 2;
        x = y = 23;


        compiler.executePipelineFromResource("test.txt", null);
    }
}