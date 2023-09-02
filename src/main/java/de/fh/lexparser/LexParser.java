package de.fh.lexparser;

import de.fh.javacc.generated.ParseException;
import de.fh.javacc.generated.SimpleNode;
import de.fh.javacc.generated.TestParser;

import java.io.InputStream;

public class LexParser implements ILexParser<SimpleNode> {

    public SimpleNode generateAST(InputStream is) throws ParseException {
        return TestParser.parse(is);
    }
}
