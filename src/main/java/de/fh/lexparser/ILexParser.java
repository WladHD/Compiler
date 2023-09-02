package de.fh.lexparser;

import de.fh.javacc.generated.Node;
import de.fh.javacc.generated.ParseException;

import java.io.InputStream;

public interface ILexParser<T extends Node> {
    T generateAST(InputStream is) throws ParseException;
}
