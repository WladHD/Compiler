package de.fh.lexparser;

import de.fh.javacc.generated.ParseException;
import de.fh.javacc.generated.TestParser;
import de.fh.javacc.generated.SimpleNode;

import java.io.InputStream;

public class LexParser {
    public SimpleNode generateAST(InputStream is) throws ParseException {
        return TestParser.parse(is);
    }

    public SimpleNode generateASTFromResource(String name) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classloader.getResourceAsStream(name)) {
            return generateAST(is);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
