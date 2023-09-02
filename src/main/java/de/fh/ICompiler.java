package de.fh;

import de.fh.javacc.generated.Node;
import de.fh.lexparser.ILexParser;
import de.fh.semantic.ISemanticAnalyzer;
import de.fh.translator.ITranslator;

import java.io.InputStream;

public interface ICompiler<T extends Node> {

    ILexParser<T> getLexParser();

    ISemanticAnalyzer<T> getSemanticAnalyzer();

    ITranslator<T> getTranslator();

    void executePipeline(InputStream is, String outputPath);

    void executePipelineFromResource(String resourceName, String outputPath);

}
