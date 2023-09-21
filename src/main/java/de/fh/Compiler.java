package de.fh;

import de.fh.javacc.generated.Node;
import de.fh.javacc.generated.ParseException;
import de.fh.javacc.generated.SimpleNode;
import de.fh.lexparser.ILexParser;
import de.fh.semantic.ISemanticAnalyzer;
import de.fh.semantic.err.SemanticException;
import de.fh.translator.ITranslator;
import de.fh.utils.PrettyPrintVisitor;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

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
            System.out.println("[RUNNING IN DEBUG MODE (verbose mode)]");
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
            getSemanticAnalyzer().doSemanticCheck(rootNode);
        }

        System.out.println("Erfolgreich");


        if (getTranslator() != null) {
            System.out.println("Starte Übersetzung zu Java Source Code ... ");
            String javaCodeSource = getTranslator().getJavaCode(rootNode);
            System.out.println(javaCodeSource);
            System.out.println("Erfolgreich");

            /* System.out.println("Starte Übersetzung zu Java Byte Code ... ");
            byte[] javaCodeCompiler = getTranslator().getJavaByteCode(javaCodeSource);
            System.out.println("Erfolgreich");
            // TODO SAVE BYTECODE ... maybe source as well?*/

            if (outputPath != null) {
                System.out.println("Speichere generierten Java Source Code ... ");

                File outputFile = new File(outputPath);

                if (!outputFile.getParentFile().exists()) {
                    outputFile.getParentFile().mkdir();
                }

                try (PrintWriter out = new PrintWriter(outputPath)) {
                    out.println(javaCodeSource);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                System.out.printf("Unter '%s' gespeichert%n", outputPath);

                System.out.println("Generiere ausführbare JAR Datei ...");
                compileJavaSourceCode(outputFile);
            }
        }


    }

    public String getNameWithoutExtension(File f) {
        int dotIndex = f.getName().lastIndexOf('.');
        return (dotIndex == -1) ? f.getName() : f.getName().substring(0, dotIndex);
    }

    public void compileJavaSourceCode(File outputFile) {
        String nameWithoutExtension = getNameWithoutExtension(outputFile);

        System.out.println(nameWithoutExtension);
        File output = new File(outputFile.getParentFile().getAbsolutePath() + File.separator + nameWithoutExtension + ".jar");

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int compilationResult = compiler.run(null, null, null, outputFile.getAbsolutePath());

        if (compilationResult == 0) {
            System.out.println("Compilation is successful");
        } else {
            System.err.println("Compilation failed");
            return;
        }

        File[] classes = Arrays.stream(Objects.requireNonNull(outputFile.getParentFile().listFiles()))
                .filter(file -> file.isFile() && file.getName().endsWith(".class") && file.getName().contains(nameWithoutExtension))
                .toArray(File[]::new);

        System.out.println("Found " + classes.length + " classes ... generating jar");

        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, "TranslatorTemplate");

        JarOutputStream jos;

        try {
            jos = new JarOutputStream(new FileOutputStream(output), manifest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        for (File cl : classes) {
            try {
                addFile(jos, cl);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            jos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Deleting raw temporary class files ...");
        for(File deleteMe : classes)
            deleteMe.delete();

        System.out.println("JAR Datei wurde generiert... ausführbar mit:");
        System.out.printf("java -jar \"%s\"%n", output.getAbsolutePath());
    }

    // @author https://www.baeldung.com/jar-create-programatically
    public void addFile(JarOutputStream target, File cl) throws IOException {
        JarEntry entry = new JarEntry(cl.getName());
        entry.setTime(cl.lastModified());
        target.putNextEntry(entry);

        BufferedInputStream in = new BufferedInputStream(new FileInputStream(cl));
        byte[] buffer = new byte[1024];
        while (true) {
            int count = in.read(buffer);
            if (count == -1) {
                break;
            }
            target.write(buffer, 0, count);
        }
        target.closeEntry();
        in.close();
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
