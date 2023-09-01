package de.fh;

import de.fh.javacc.generated.SimpleNode;
import de.fh.lexparser.LexParser;

public class Main {
    public static void main(String[] args) {
        LexParser lp = new LexParser();

        SimpleNode sn = lp.generateASTFromResource("test.txt");

        sn.dump("");
    }
}