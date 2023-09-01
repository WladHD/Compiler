package de.fh.lexparser;

import de.fh.javacc.generated.ParseException;
import de.fh.javacc.generated.SimpleNode;
import de.fh.javacc.generated.TestParser;

import java.io.InputStream;

public class LexParser implements ILexParser<SimpleNode> {

    public SimpleNode generateAST(InputStream is) throws ParseException {
        return TestParser.parse(is);
    }

    public SimpleNode generateASTFromResource(String name) throws ParseException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classloader.getResourceAsStream(name)) {
            return generateAST(is);
        } catch (ParseException pe) {
            throw pe;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
