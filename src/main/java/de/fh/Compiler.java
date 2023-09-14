package de.fh;

import de.fh.javacc.generated.Node;
import de.fh.javacc.generated.ParseException;
import de.fh.javacc.generated.SimpleNode;
import de.fh.lexparser.ILexParser;
import de.fh.semantic.ISemanticAnalyzer;
import de.fh.semantic.err.SemanticException;
import de.fh.translator.ITranslator;
import de.fh.utils.PrettyPrintVisitor;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;

public class Compiler<T extends Node> implements ICompiler<T> {
    private final ILexParser<T> lexParser;
    private final ISemanticAnalyzer<T> semanticAnalyzer;
    private final ITranslator<T> translator;

    private final boolean debug;

    public Compiler(ILexParser<T> lexParser, ISemanticAnalyzer<T> semanticAnalyzer, ITranslator<T> translator, boolean debug) {
        this.lexParser = lexParser;
        this.semanticAnalyzer = semanticAnalyzer;
        this.translator = translator;
        this.debug = debug;
    }

    @Override
    public ILexParser<T> getLexParser() {
        return lexParser;
    }

    @Override
    public ISemanticAnalyzer<T> getSemanticAnalyzer() {
        return semanticAnalyzer;
    }

    @Override
    public ITranslator<T> getTranslator() {
        return translator;
    }

    @Override
    public void executePipeline(InputStream is, String outputPath) {
        if (debug)
            System.out.println("[RUNNING IN DEBUG MODE]");
        System.out.println("Starte lexikalische und syntaktische Analyse ...");
        T rootNode;

        try {
            rootNode = getLexParser().generateAST(is);

            if (debug && rootNode instanceof SimpleNode s)
                PrettyPrintVisitor.dump(s);

        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("Erfolgreich");
        System.out.println("Starte semantische Analyse und Fehlerprüfung ... ");

        if (getSemanticAnalyzer() != null) {
            try {
                getSemanticAnalyzer().doSemanticCheck(rootNode);
            } catch (SemanticException e) {
                System.out.println(e.getMessage());
                return;
            }
        }

        System.out.println("Erfolgreich");


        if (getTranslator() != null) {
            System.out.println("Starte Übersetzung zu Java Source Code ... ");
            String javaCodeSource = getTranslator().getJavaCode(rootNode);
            System.out.println(javaCodeSource);
            System.out.println("Erfolgreich");
            System.out.println("Starte Übersetzung zu Java Byte Code ... ");
            byte[] javaCodeCompiler = getTranslator().getJavaByteCode(javaCodeSource);
            System.out.println("Erfolgreich");
            // TODO SAVE BYTECODE ... maybe source as well?

            System.out.printf("Unter '%s' gespeichert%n", outputPath);

            try(PrintWriter out = new PrintWriter("./src/test/java/Translated.java")) {
                out.println(javaCodeSource);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }



    }

    @Override
    public void executePipelineFromResource(String resourceName, String outputPath) {
        System.out.printf("Lese Ressource %s ein ...%n", resourceName);
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classloader.getResourceAsStream(resourceName)) {
            executePipeline(is, outputPath);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
