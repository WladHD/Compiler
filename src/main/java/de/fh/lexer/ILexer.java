package de.fh.lexer;

public interface ILexer<T> {

    T generateTree(String source);

}
