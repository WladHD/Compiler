package de.fh.semantic;

import de.fh.javacc.generated.SimpleNode;
import de.fh.semantic.closure.Closure;
import de.fh.semantic.err.SemanticException;
import de.fh.semantic.treevisitor.SemanticTreeVisitor;
import de.fh.translator.ITranslator;

public class SemanticAnalyzer implements ISemanticAnalyzer<SimpleNode> {

    @Override
    public void doSemanticCheck(SimpleNode rootNode, ITranslator<SimpleNode> itr) throws SemanticException {
        SemanticTreeVisitor stv = new SemanticTreeVisitor(new Closure<>(null), itr);

        stv.setupRootClosure();


        /*
        CHECK:
            Variable redeclaration
            Variable init without declaration
            Method redeclaration
         */
        stv.visit(rootNode, stv.getRootClosure());
    }
}
