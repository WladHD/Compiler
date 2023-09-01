package de.fh;

import de.fh.javacc.generated.ParseException;
import de.fh.javacc.generated.SimpleNode;
import de.fh.lexparser.LexParser;

public class Main {
    public static void main(String[] args) throws ParseException {
        LexParser lp = new LexParser();

        SimpleNode sn = lp.generateASTFromResource("test.txt");

        sn.dump("");
    }
}