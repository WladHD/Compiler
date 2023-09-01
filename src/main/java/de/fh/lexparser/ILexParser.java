package de.fh.lexparser;

import de.fh.javacc.generated.ParseException;

import java.io.InputStream;

public interface ILexParser<T> {
    T generateAST(InputStream is) throws ParseException;

    T generateASTFromResource(String name) throws ParseException;
}
