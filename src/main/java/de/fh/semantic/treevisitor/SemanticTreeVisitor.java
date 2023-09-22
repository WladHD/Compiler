package de.fh.semantic.treevisitor;

import de.fh.Main;
import de.fh.javacc.generated.*;
import de.fh.semantic.ComplexParserType;
import de.fh.semantic.ComplexParserTypeIdentifier;
import de.fh.semantic.ParserTypes;
import de.fh.semantic.closure.IClosure;
import de.fh.semantic.err.*;
import de.fh.utils.GodlyTestParserVisitor;

import java.util.ArrayList;

public class SemanticTreeVisitor implements GodlyTestParserVisitor {

    private final IClosure<String, ComplexParserType, SimpleNode> rootClosure;

    public SemanticTreeVisitor(IClosure<String, ComplexParserType, SimpleNode> closure) {
        rootClosure = closure;
    }

    public IClosure<String, ComplexParserType, SimpleNode> getRootClosure() {
        return rootClosure;
    }

    private IClosure<String, ComplexParserType, SimpleNode> cast(Object b) {
        if (!rootClosure.getClass().isInstance(b))
            throw new InternClosureCastingSemanticException();

        return (IClosure<String, ComplexParserType, SimpleNode>) b;
    }

    private String concatChildrenToText(SimpleNode root, IClosure<String, ComplexParserType, SimpleNode> closure, String separator) {
        StringBuilder sb = new StringBuilder();

        boolean first = true;

        if (root.jjtGetNumChildren() != 0) {
            for (int i = 0; i < root.jjtGetNumChildren(); ++i) {
                SimpleNode n = (SimpleNode) root.jjtGetChild(i);
                Object val = visit(n, closure);

                if (val == null)
                    continue;

                if (!first && separator != null)
                    sb.append(separator);

                sb.append(val);

                first = false;
            }
        }

        return sb.toString();
    }

    @Override
    public Object visit(ASTPROGRAM node, Object data) {
        visitAll(node, data);
        return null;
    }

    @Override
    public Object visit(ASTST_RETURN node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTST_IF node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTST_IF_COND node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTST_IF_EXPR node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTST_ELSE_IF node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTST_ELSE_EXPR node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTST_WHILE node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTST_WHILE_COND node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTST_WHILE_EXPR node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTST_FOR node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTST_FOR_EXPR node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTST_FOR_EACH_BODY node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTST_FOR_EACH_BODY_DECL node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTST_FOR_EACH_BODY_ARR node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTST_FOR_NORMAL_BODY node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTST_FOR_NORMAL_BODY_DECL node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTST_FOR_NORMAL_BODY_COND node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTST_FOR_NORMAL_BODY_INC node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMETHOD_DECLARATION node, Object data) {
        ASTTYPE at = (ASTTYPE) node.jjtGetChild(0);
        ASTLITERAL_IDENTIFIER ident = (ASTLITERAL_IDENTIFIER) node.jjtGetChild(1);

        String identifier = (String) ident.jjtGetValue();
        ComplexParserType type = (ComplexParserType) visit(at, data);

        ASTMETHOD_PARAMETERS params = (ASTMETHOD_PARAMETERS) node.jjtGetChild(2);
        ASTBLOCK block = (ASTBLOCK) node.jjtGetChild(3);

        IClosure<String, ComplexParserType, SimpleNode> ic = cast(data).addMethod(identifier, type);

        visit(params, ic);

        if (!type.equals("void")) {
            boolean hasReturn = false;
            ArrayList<SimpleNode> snArr = childrenToArray(block);

            for (int i = 0; i < snArr.size(); i++)
                if (snArr.get(i) instanceof ASTST_RETURN) {
                    hasReturn = true;

                    if (i + 1 != snArr.size()) {
                        throw new UnreachableCodeSemanticException();
                    }
                    break;
                }

            if (!hasReturn)
                throw new NoReturnSemanticException();
        }
        Main.logger.info("Method: Put: " + identifier + " with return type: " + type);

        // Ist extra so, damit kein neuer Closure beim Block angelegt wird
        visitAll(block, ic);
        return null;
    }

    @Override
    public Object visit(ASTMETHOD_PARAMETERS node, Object data) {
        visitAll(node, data);
        return null;
    }

    @Override
    public Object visit(ASTMETHOD_PARAM node, Object data) {
        ASTTYPE at = (ASTTYPE) node.jjtGetChild(0);
        ASTLITERAL_IDENTIFIER ident = (ASTLITERAL_IDENTIFIER) node.jjtGetChild(1);

        ComplexParserType type = (ComplexParserType) visit(at, data);
        String identifier = (String) ident.jjtGetValue();

        cast(data).addVariableDeclaration(identifier, type, true);
        return null;
    }

    // TODO initialisation
    @Override
    public Object visit(ASTVAR_DECLARATION node, Object data) {
        ASTTYPE at = (ASTTYPE) node.jjtGetChild(0);
        ComplexParserType type = (ComplexParserType) visit(at, data);

        String identifier;
        ASTVAR_INIT init = null;

        if (node.jjtGetChild(1) instanceof ASTLITERAL_IDENTIFIER ident) {
            identifier = (String) ident.jjtGetValue();
        } else {
            init = (ASTVAR_INIT) node.jjtGetChild(1);
            ASTLITERAL_IDENTIFIER ident = (ASTLITERAL_IDENTIFIER) init.jjtGetChild(0);
            identifier = (String) ident.jjtGetValue();
        }

        cast(data).addVariableDeclaration(identifier, type, false);
        Main.logger.info("Put: " + identifier + " with type: " + type);

        if (init != null)
            visit(init, data);

        return null;
    }

    @Override
    public Object visit(ASTVAR_INIT node, Object data) {
        ASTLITERAL_IDENTIFIER ident = (ASTLITERAL_IDENTIFIER) node.jjtGetChild(0);
        ASTOP_PRIO_1 operation = (ASTOP_PRIO_1) node.jjtGetChild(1);

        String identifier = (String) ident.jjtGetValue();

        if (!cast(data).hasVariable(identifier, false))
            throw new VariableNotDeclaredSemanticException(identifier);

        // TODO check operation return type

        Object typeReturnType = visit(operation, data);
        ComplexParserType expectedType = cast(data).getVariableTypeAndValue(identifier, false).getKey();

        if (!expectedType.isEqual(typeReturnType)) {
            throw new ExpectedTypeMissmatchSemanticException(expectedType, typeReturnType);
        }

        return expectedType;
    }

    @Override
    public Object visit(ASTBLOCK node, Object data) {

        IClosure<String, ComplexParserType, SimpleNode> ic = cast(data).createNewChildClosure();

        visitAll(node, ic);

        return null;
    }

    @Override
    public Object visit(ASTOP_PRIO_1 node, Object data) {
        if (node.jjtGetNumChildren() == 1)
            return visit(node.jjtGetChild(0), data);

        // TODO is it even possible?
        return null;
    }

    @Override
    public Object visit(ASTOP_PRIO_3 node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOPERATOR_3 node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOP_PRIO_4 node, Object data) {
        return inferOperatorFromTwoArguments(node, data);
    }

    @Override
    public Object visit(ASTOPERATOR_4 node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOP_PRIO_6 node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOPERATOR_6 node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOP_PRIO_8 node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOPERATOR_8 node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOP_PRIO_9 node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOPERATOR_9 node, Object data) {
        return null;
    }

    public ComplexParserType inferOperatorFromTwoArguments(SimpleNode node, Object data) {
        ComplexParserType currentType = (ComplexParserType) visit(node.jjtGetChild(0), data);

        for (int i = 1; i < node.jjtGetNumChildren(); i += 2) {
            ComplexParserType cpt2 = (ComplexParserType) visit(node.jjtGetChild(i + 1), data);
            currentType = ComplexParserTypeIdentifier.inferDatatype(currentType, cpt2, (String) ((SimpleNode) node.jjtGetChild(i)).jjtGetValue());
        }

        Main.logger.info(currentType.toString());

        return currentType;
    }

    @Override
    public Object visit(ASTOP_PRIO_11 node, Object data) {
        return inferOperatorFromTwoArguments(node, data);
    }

    @Override
    public Object visit(ASTOPERATOR_11 node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOPERATOR_12 node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOP_PRIO_12 node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOP_PRIO_13 node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOPERATOR_13 node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOP_PRIO_14 node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOPERATOR_14 node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOP_PRIO_15 node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOPERATOR_15_METHOD_CALL node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOPERATOR_15_METHOD_CALL_PARAMS node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOPERATOR_15_METHOD_CALL_PARAM node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOPERATOR_15_VARIABLE_CALL node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOPERATOR_15_ARRAY_INDEX_CALL node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTLITERAL_IDENTIFIER node, Object data) {
        return cast(data).getVariableTypeAndValue((String) node.jjtGetValue(), false).getKey();
    }

    @Override
    public Object visit(ASTTYPE_ARRAY node, Object data) {
        ((ComplexParserType) data).setArray(true);
        return null;
    }

    @Override
    public Object visit(ASTTYPE_TYPESAFE node, Object data) {
        ComplexParserType cpt = (ComplexParserType) data;

        for (SimpleNode sn : childrenToArray(node))
            cpt.addComplexType((ComplexParserType) visit(sn, cpt));

        return null;
    }

    @Override
    public Object visit(ASTTYPE node, Object data) {
        ComplexParserType cpt = new ComplexParserType(ParserTypes.match((String) node.jjtGetValue()));

        Main.logger.info((String) node.jjtGetValue());
        Main.logger.info(cpt.toString());

        for (SimpleNode sn : childrenToArray(node))
            visit(sn, cpt);

        return cpt;
    }

    @Override
    public Object visit(ASTARRAY_CONTAINER_NATIVE node, Object data) {
        if (node.jjtGetNumChildren() == 0)
            return new ComplexParserType(ParserTypes.EMPTY_ARRAY_CONTAINER);

        ArrayList<Object> cpt = new ArrayList<>();

        for (SimpleNode sn : childrenToArray(node))
            cpt.add(visit(sn, data));

        return cpt;
    }

    @Override
    public Object visit(ASTARRAY_CONTAINER node, Object data) {
        if (node.jjtGetNumChildren() == 0)
            return new ComplexParserType(ParserTypes.EMPTY_ARRAY_CONTAINER);

        ArrayList<Object> cpt = new ArrayList<>();

        for (SimpleNode sn : childrenToArray(node))
            cpt.add(visit(sn, data));

        return cpt;
    }

    @Override
    public Object visit(ASTARRAY_ELEMENT node, Object data) {
        return visit(node.jjtGetChild(0), data);
    }

    @Override
    public Object visit(ASTMAP_ELEMENT node, Object data) {

        ArrayList<Object> cpt = new ArrayList<>();

        for(SimpleNode sn : childrenToArray(node))
            cpt.add(visit(sn, data));

        return cpt;
    }

    @Override
    public Object visit(ASTMAP_ELEMENT_KEY node, Object data) {
        return visit(node.jjtGetChild(0), data);
    }

    @Override
    public Object visit(ASTMAP_ELEMENT_VALUE node, Object data) {
        return visit(node.jjtGetChild(0), data);
    }

    @Override
    public Object visit(ASTLITERAL_INTEGER node, Object data) {
        return new ComplexParserType(ParserTypes.INT);
    }

    @Override
    public Object visit(ASTLITERAL_BOOLEAN node, Object data) {
        return new ComplexParserType(ParserTypes.BOOLEAN);
    }

    @Override
    public Object visit(ASTLITERAL_CHAR node, Object data) {
        return new ComplexParserType(ParserTypes.CHAR);
    }

    @Override
    public Object visit(ASTLITERAL_STRING node, Object data) {
        return new ComplexParserType(ParserTypes.STRING);
    }

    @Override
    public Object visit(ASTLITERAL_PATH node, Object data) {
        return new ComplexParserType(ParserTypes.PATH);
    }
}
