package de.fh.semantic.treevisitor;

import de.fh.Main;
import de.fh.javacc.generated.*;
import de.fh.semantic.ComplexParserType;
import de.fh.semantic.ComplexParserTypeIdentifier;
import de.fh.semantic.ParserTypes;
import de.fh.semantic.closure.IClosure;
import de.fh.semantic.err.*;
import de.fh.translator.ITranslator;
import de.fh.utils.GodlyTestParserVisitor;

import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;

public class SemanticTreeVisitor implements GodlyTestParserVisitor {

    private IClosure<String, ComplexParserType, Object> rootClosure;
    private final ITranslator<SimpleNode> translatorVisitor;

    boolean translateDisabled = false;
    boolean identNext = false;
    int ident = 0;

    public void translate(Object s) {
        translate(s, true);
    }

    public void translate(Object s, boolean checkForIdent) {
        if (translateDisabled)
            return;

        if (s.toString().contains("}") && checkForIdent) {
            ident -= 2;
            identNext = true;
        }

        if (identNext && checkForIdent) {
            for (int i = 0; i < ident; i++)
                translatorVisitor.add(" ");
            identNext = false;
        }

        if (s.toString().contains("}") && checkForIdent) {
            identNext = true;
        }

        translatorVisitor.add(s);

        if (s.toString().contains("{") && checkForIdent) {
            ident += 2;
            identNext = true;
        }

        if (s.toString().contains(";") && checkForIdent)
            identNext = true;
    }

    public void translateSemicolon() {
        translate(";");
        translateNewLine();
    }

    public void translateNewLine() {
        if (translateDisabled)
            return;

        translatorVisitor.add("\n");
    }

    public SemanticTreeVisitor(IClosure<String, ComplexParserType, Object> closure, ITranslator<SimpleNode> itr) {
        rootClosure = closure;
        translatorVisitor = itr;
    }

    public IClosure<String, ComplexParserType, Object> getRootClosure() {
        return rootClosure;
    }

    public IClosure<String, ComplexParserType, Object> retrieveClassClosure(String type) {
        return (IClosure<String, ComplexParserType, Object>) (getRootClosure().getParent() == null ? getRootClosure() : getRootClosure().getParent()).getVariableTypeAndValue(type, true).getValue();
    }

    private IClosure<String, ComplexParserType, Object> cast(Object b) {
        if (!rootClosure.getClass().isInstance(b)) throw new InternClosureCastingSemanticException();

        return (IClosure<String, ComplexParserType, Object>) b;
    }

    public boolean isNodeStatement(SimpleNode node) {
        if (node instanceof ASTOP_PRIO_15 p15) {
            return p15.jjtGetChild(p15.jjtGetNumChildren() - 1) instanceof ASTOPERATOR_15_METHOD_CALL;
        }

        if (node instanceof ASTOP_PRIO_14 p14) {
            return ((SimpleNode) p14.jjtGetChild(1)).jjtGetValue().equals("++") || ((SimpleNode) p14.jjtGetChild(1)).jjtGetValue().equals("--");
        }

        if (node instanceof ASTOP_PRIO_13 p13) {
            return ((SimpleNode) p13.jjtGetChild(0)).jjtGetValue().equals("++") || ((SimpleNode) p13.jjtGetChild(0)).jjtGetValue().equals("--");
        }

        return node instanceof ASTVAR_DECLARATION || node instanceof ASTVAR_INIT;
    }

    public void checkIfNodeIsStatement(Object closure, SimpleNode node, String stmtName) {
        if (!isNodeStatement(node))
            throw new StatementExpectedSemanticException(cast(closure), stmtName);
    }

    public void setupRootClosure() {
        // PRIMITIVE TYPES
        addRootClosureClass("int");
        addRootClosureClass("Object");
        addRootClosureClass("char");
        addRootClosureClass("boolean");

        // STRING [Meth: length() int, charAt(int) char]
        Object cl_string = addRootClosureClass("String");
        addRootClosureMethod(cast(cl_string), "length", new ComplexParserType(ParserTypes.INT));
        addRootClosureMethodParameter(
                addRootClosureMethod(cast(cl_string), "charAt", new ComplexParserType(ParserTypes.CHAR)),
                new ComplexParserType(ParserTypes.INT));


        Object cl_map = addRootClosureClass("Map");
        addRootClosureMethodParameter(addRootClosureMethod(cast(cl_map), "containsKey", new ComplexParserType(ParserTypes.BOOLEAN)),
                new ComplexParserType(ParserTypes.CLASS_OBJECT));

        // SET
        Object cl_set = addRootClosureClass("Set");
        addRootClosureMethodParameter(addRootClosureMethod(cast(cl_set), "contains", new ComplexParserType(ParserTypes.BOOLEAN)),
                new ComplexParserType(ParserTypes.CLASS_OBJECT));

        Object cl_system = addRootClosureClass("System");
        addRootClosureMethod(cast(cl_system), "readString", new ComplexParserType(ParserTypes.STRING));
        Object cl_system_println = addRootClosureMethod(cast(cl_system), "println", new ComplexParserType(ParserTypes.VOID));
        addRootClosureMethodParameter(cast(cl_system_println), new ComplexParserType(ParserTypes.CLASS_OBJECT));
        Object cl_system_print = addRootClosureMethod(cast(cl_system), "print", new ComplexParserType(ParserTypes.VOID));
        addRootClosureMethodParameter(cast(cl_system_print), new ComplexParserType(ParserTypes.CLASS_OBJECT));


        // PATHS
        Object cl_path = addRootClosureClass("Path");
        addRootClosureMethod(cast(cl_path), "toString", new ComplexParserType(ParserTypes.STRING));
        addRootClosureVariable(cast(cl_path), "name", new ComplexParserType(ParserTypes.STRING).setArray(true));
        addRootClosureMethod(cast(cl_path), "remove", new ComplexParserType(ParserTypes.VOID));
        addRootClosureMethodParameter(addRootClosureMethod(cast(cl_path), "copyTo", new ComplexParserType(ParserTypes.VOID)),
                new ComplexParserType(ParserTypes.PATH));
        addRootClosureMethodParameter(addRootClosureMethod(cast(cl_path), "moveTo", new ComplexParserType(ParserTypes.VOID)),
                new ComplexParserType(ParserTypes.PATH));

        // FILES
        Object cl_files = addRootClosureClass("Files");
        addRootClosureVariable(cast(cl_files), "type", new ComplexParserType(ParserTypes.STRING));
        addRootClosureVariable(cast(cl_files), "name", new ComplexParserType(ParserTypes.STRING));
        addRootClosureVariable(cast(cl_files), "path", new ComplexParserType(ParserTypes.PATH));
        addRootClosureMethodParameter(addRootClosureMethod(cast(cl_files), "rename", new ComplexParserType(ParserTypes.VOID)),
                new ComplexParserType(ParserTypes.STRING));
        addRootClosureMethodParameter(addRootClosureMethod(cast(cl_files), "moveTo", new ComplexParserType(ParserTypes.VOID)),
                new ComplexParserType(ParserTypes.STRING));

        // PATH
        addRootClosureVariable(cast(cl_path), "files", new ComplexParserType(ParserTypes.FILES).setArray(true));


        // ALLOW NAMING OF SYSTEM ETC.
        rootClosure.setClosureName("TopLevel");
        rootClosure = getRootClosure().createNewChildClosure("Environment");
    }

    IClosure<String, ComplexParserType, Object> addRootClosureClass(String name) {
        IClosure<String, ComplexParserType, Object> systemClosure = getRootClosure().createNewChildClosure(name);
        getRootClosure().addVariableDeclaration(name, new ComplexParserType(ParserTypes.CLASS_OBJECT));
        getRootClosure().addVariableInitialisation(name, systemClosure);

        return systemClosure;
    }

    IClosure<String, ComplexParserType, Object> addRootClosureMethod(IClosure<String, ComplexParserType, Object> classClosure, String name, ComplexParserType type) {
        return classClosure.addMethod(name, type);
    }

    IClosure<String, ComplexParserType, Object> addRootClosureVariable(IClosure<String, ComplexParserType, Object> methodClosure, String name, ComplexParserType type) {
        methodClosure.addVariableDeclaration(name, type);
        methodClosure.addVariableInitialisation(name, retrieveClassClosure(type.getBasicType().toString()));

        return methodClosure;
    }

    IClosure<String, ComplexParserType, Object> addRootClosureMethodParameter(IClosure<String, ComplexParserType, Object> methodClosure, ComplexParserType... type) {
        for (int i = 0; i < type.length; i++)
            methodClosure.addVariableDeclaration("ignored" + i, type[i], true, retrieveClassClosure(type[i].getBasicType().toString()));

        return methodClosure;
    }

    // TODO delete?
    private String concatChildrenToText(SimpleNode root, IClosure<String, ComplexParserType, Object> closure, String separator) {
        StringBuilder sb = new StringBuilder();

        boolean first = true;

        if (root.jjtGetNumChildren() != 0) {
            for (int i = 0; i < root.jjtGetNumChildren(); ++i) {
                SimpleNode n = (SimpleNode) root.jjtGetChild(i);
                Object val = visit(n, closure);

                if (val == null) continue;

                if (!first && separator != null) sb.append(separator);

                sb.append(val);

                first = false;
            }
        }

        return sb.toString();
    }

    @Override
    public Object visit(ASTPROGRAM node, Object data) {
        for (SimpleNode sn : childrenToArray(node)) {
            if (!(sn instanceof ASTBLOCK || sn instanceof ASTSEMICOLON || sn instanceof ASTVAR_DECLARATION || sn instanceof ASTVAR_INIT || sn instanceof ASTMETHOD_DECLARATION))
                throw new IllegalClassContentSemanticException(cast(data));

            visit(sn, data);
        }
        return null;
    }

    @Override
    public Object visit(ASTSEMICOLON node, Object data) {
        translateSemicolon();
        return null;
    }

    @Override
    public Object visit(ASTST_RETURN node, Object data) {
        translate("return" + (node.jjtGetNumChildren() != 0 ? " " : ""));
        SimpleNode parent = (SimpleNode) node.jjtGetParent();
        ArrayList<SimpleNode> snArr = childrenToArray(parent);

        for (int i = 0; i < snArr.size(); i++)
            if (snArr.get(i) instanceof ASTST_RETURN)
                if (i + 2 != snArr.size()) // BECAUSE OF SEMICOLON
                    throw new UnreachableCodeSemanticException(cast(data));

        ComplexParserType cpt = node.jjtGetNumChildren() == 0 ? new ComplexParserType(ParserTypes.VOID) : (ComplexParserType) visit(node.jjtGetChild(0), data);

        while (!(parent instanceof ASTMETHOD_DECLARATION)) {
            if (parent.jjtGetParent() != null)
                parent = (SimpleNode) parent.jjtGetParent();
            else break;
        }

        if (parent instanceof ASTMETHOD_DECLARATION meth) {
            ComplexParserType meth_cpt = (ComplexParserType) visit(meth.jjtGetChild(0), data);

            if (!meth_cpt.isEqual(cpt))
                throw new ReturnTypeMismatchSemanticException(cast(data), (String) ((SimpleNode) meth.jjtGetChild(1)).jjtGetValue(), meth_cpt, cpt);
        } else
            throw new UnknownSemanticException(null);

        return cpt;
    }

    @Override
    public Object visit(ASTST_IF node, Object data) {
        translate("if ");
        visitChildren(node, data);
        return null;
    }

    @Override
    public Object visit(ASTST_IF_COND node, Object data) {
        translate("(");
        checkForBooleanType(node, data);
        translate(") ");
        return null;
    }

    @Override
    public Object visit(ASTST_IF_EXPR node, Object data) {
        checkForVarDeclarationInExpression(data, node, "if");
        visitChildren(node, data);
        return null;
    }

    @Override
    public Object visit(ASTST_ELSE_IF node, Object data) {
        translate("else if ");
        visitChildren(node, data);
        return null;
    }

    @Override
    public Object visit(ASTST_ELSE_EXPR node, Object data) {
        translate("else ");
        checkForVarDeclarationInExpression(data, node, "if");
        visitChildren(node, data);
        return null;
    }

    public void checkForVarDeclarationInExpression(Object closure, SimpleNode node, String name) {
        if (node.jjtGetChild(0) instanceof ASTVAR_DECLARATION)
            throw new VariableDeclarationNotAllowedSemanticException(cast(closure), ((SimpleNode) node.jjtGetChild(0).jjtGetChild(1).jjtGetChild(0)).jjtGetValue().toString(), name);
    }

    public void checkForBooleanType(SimpleNode node, Object data) {
        ComplexParserType cpt = (ComplexParserType) visit(node.jjtGetChild(0), data);

        if (!cpt.isEqual(ParserTypes.BOOLEAN))
            throw new ExpectedTypeMissmatchSemanticException(cast(data), new ComplexParserType(ParserTypes.BOOLEAN), cpt);
    }

    @Override
    public Object visit(ASTST_WHILE node, Object data) {
        translate("while ");
        visitChildren(node, data);
        return null;
    }

    @Override
    public Object visit(ASTST_WHILE_COND node, Object data) {
        translate("(");
        checkForBooleanType(node, data);
        translate(") ");
        return null;
    }

    @Override
    public Object visit(ASTST_WHILE_EXPR node, Object data) {
        checkForVarDeclarationInExpression(data, node, "while");

        visitChildren(node, data);
        return null;
    }

    @Override
    public Object visit(ASTST_FOR node, Object data) {
        translate("for ");
        IClosure<String, ComplexParserType, Object> iClosure = cast(data).createNewChildClosure("for-loop");
        visitChildren(node, iClosure);
        return null;
    }

    @Override
    public Object visit(ASTST_FOR_EXPR node, Object data) {
        checkForVarDeclarationInExpression(data, node, "for");

        visitChildren(node, data);
        return null;
    }

    @Override
    public Object visit(ASTST_FOR_EACH_BODY node, Object data) {
        ASTST_FOR_EACH_BODY_DECL decl = (ASTST_FOR_EACH_BODY_DECL) node.jjtGetChild(0);
        ASTST_FOR_EACH_BODY_ARR init = (ASTST_FOR_EACH_BODY_ARR) node.jjtGetChild(1);

        translate("(");
        ComplexParserType definedType = (ComplexParserType) visit(decl, data);
        translate(" : ");
        ComplexParserType currentType = degradeArrayMapSet((ComplexParserType) visit(init, data), true);
        translate(") ");
        String varName = (String) ((SimpleNode) decl.jjtGetChild(1)).jjtGetValue();
        cast(data).addVariableDeclaration(varName, definedType);
        cast(data).addVariableInitialisation(varName, retrieveClassClosure(definedType.getBasicType().toString()));

        if (!currentType.isEqual(definedType))
            throw new ExpectedTypeMissmatchSemanticException(cast(data), currentType, definedType);

        return null;
    }

    public ComplexParserType degradeArrayMapSet(ComplexParserType currentType, boolean mapCaseReturnKey) {
        if (currentType.isArray()) {
            currentType = currentType.clone();
            currentType.setArray(false);
        } else if (currentType.getBasicType() == ParserTypes.SET) {
            currentType = currentType.getComplexParserTypes().get(0);
        } else if (currentType.getBasicType() == ParserTypes.MAP) {
            currentType = currentType.getComplexParserTypes().get(mapCaseReturnKey ? 0 : 1);
        }

        return currentType;
    }

    @Override
    public Object visit(ASTST_FOR_EACH_BODY_DECL node, Object data) {

        ComplexParserType cpt = (ComplexParserType) visit(node.jjtGetChild(0), data);

        translate(cpt.toStringJava(true) + " " + ((SimpleNode) node.jjtGetChild(1)).jjtGetValue());

        return cpt;
    }

    @Override
    public Object visit(ASTST_FOR_EACH_BODY_ARR node, Object data) {
        return visit(node.jjtGetChild(0), data);
    }

    @Override
    public Object visit(ASTST_FOR_NORMAL_BODY node, Object data) {
        translate("(");
        visitChildren(node, data);
        translate(") ");
        return null;
    }

    @Override
    public Object visit(ASTST_FOR_NORMAL_BODY_DECL node, Object data) {
        if (node.jjtGetNumChildren() != 0)
            checkIfNodeIsStatement(data, (SimpleNode) node.jjtGetChild(0), "if (erstes Argument)");

        visitChildren(node, data);
        translate("; ", false);
        return null;
    }

    @Override
    public Object visit(ASTST_FOR_NORMAL_BODY_COND node, Object data) {
        if (node.jjtGetNumChildren() != 0)
            checkForBooleanType(node, data);

        translate("; ", false);

        return null;
    }

    @Override
    public Object visit(ASTST_FOR_NORMAL_BODY_INC node, Object data) {
        if (node.jjtGetNumChildren() != 0)
            checkIfNodeIsStatement(data, (SimpleNode) node.jjtGetChild(0), "if (drittes Argument)");

        visitChildren(node, data);
        return null;
    }

    @Override
    public Object visit(ASTMETHOD_DECLARATION node, Object data) {
        translateNewLine();
        ASTTYPE at = (ASTTYPE) node.jjtGetChild(0);
        ASTLITERAL_IDENTIFIER ident = (ASTLITERAL_IDENTIFIER) node.jjtGetChild(1);

        String identifier = (String) ident.jjtGetValue();
        ComplexParserType type = (ComplexParserType) visit(at, data);

        translate(MessageFormat.format("public {0} {1}(", type.toStringJava(true), identifier));

        ASTMETHOD_PARAMETERS params = (ASTMETHOD_PARAMETERS) node.jjtGetChild(2);
        ASTBLOCK block = (ASTBLOCK) node.jjtGetChild(3);

        IClosure<String, ComplexParserType, Object> ic = cast(data).addMethod(identifier, type);

        visit(params, ic);

        translate(") {");
        translateNewLine();

        if (type.getBasicType() != ParserTypes.VOID) {
            boolean hasReturn = false;
            ArrayList<SimpleNode> snArr = childrenToArray(block);

            for (SimpleNode simpleNode : snArr)
                if (simpleNode instanceof ASTST_RETURN) {
                    hasReturn = true;
                    break;
                }

            if (!hasReturn) throw new NoReturnSemanticException(cast(data));
        }

        Main.logger.info(MessageFormat.format("[Closure] Added method declaration {0} with return type {1}.", identifier, type));

        // Ist extra so, damit kein neuer Closure beim Block angelegt wird
        visitChildren(block, ic);

        translate("}");
        translateNewLine();
        translateNewLine();
        return null;
    }

    @Override
    public Object visit(ASTMETHOD_PARAMETERS node, Object data) {
        boolean first = true;
        for (SimpleNode sn : childrenToArray(node)) {
            if (!first)
                translate(", ");
            visit(sn, data);
            first = false;
        }
        return null;
    }

    @Override
    public Object visit(ASTMETHOD_PARAM node, Object data) {
        ASTTYPE at = (ASTTYPE) node.jjtGetChild(0);
        ASTLITERAL_IDENTIFIER ident = (ASTLITERAL_IDENTIFIER) node.jjtGetChild(1);

        ComplexParserType type = (ComplexParserType) visit(at, data);
        String identifier = (String) ident.jjtGetValue();

        translate(type.toStringJava(true) + " " + identifier);

        cast(data).addVariableDeclaration(identifier, type, true, retrieveClassClosure(type.getBasicType().toString()));
        return null;
    }

    @Override
    public Object visit(ASTVAR_DECLARATION node, Object data) {
        ASTTYPE at = (ASTTYPE) node.jjtGetChild(0);
        ComplexParserType type = (ComplexParserType) visit(at, data);

        String identifier;
        ASTVAR_INIT init = null;
        translate(type.toStringJava(true) + " ");

        if (node.jjtGetChild(1) instanceof ASTLITERAL_IDENTIFIER ident) {
            identifier = (String) ident.jjtGetValue();
            translate(identifier);
        } else {
            init = (ASTVAR_INIT) node.jjtGetChild(1);
            ASTLITERAL_IDENTIFIER ident = (ASTLITERAL_IDENTIFIER) init.jjtGetChild(0);
            identifier = (String) ident.jjtGetValue();
        }

        cast(data).addVariableDeclaration(identifier, type);

        if (init != null) visit(init, data);

        return null;
    }

    // TODO check operation in TypeMaps
    @Override
    public Object visit(ASTVAR_INIT node, Object data) {
        ASTLITERAL_IDENTIFIER ident = (ASTLITERAL_IDENTIFIER) node.jjtGetChild(0);
        ASTOP_PRIO_1 operation = (ASTOP_PRIO_1) node.jjtGetChild(1);

        String identifier = (String) ident.jjtGetValue();

        translate(identifier + " ");

        if (!cast(data).hasVariable(identifier, false))
            throw new VariableNotDeclaredSemanticException(cast(data), identifier);

        ComplexParserType expectedType = cast(data).getVariableTypeAndValue(identifier, false).getKey();
        Object typeReturnType = visit(operation, data);
        cast(data).addVariableInitialisation(identifier, retrieveClassClosure(expectedType.getBasicType().toString()));


        if (typeReturnType instanceof ComplexParserType cpt)
            ComplexParserTypeIdentifier.inferDatatypeFromOperation(cast(data), expectedType, cpt, operation.jjtGetValue().toString());
        else if (!operation.jjtGetValue().equals("=")) {
            if (!expectedType.isEqual(typeReturnType)) {
                throw new ExpectedTypeMissmatchSemanticException(cast(data), expectedType, typeReturnType);
            }

            throw new IllegalOperationSemanticException(cast(data), expectedType, expectedType, operation.jjtGetValue().toString());
        } else if (expectedType.isArray() || expectedType.getBasicType() == ParserTypes.SET || expectedType.getBasicType() == ParserTypes.MAP) {
            if (!expectedType.isEqual(typeReturnType)) {
                throw new ExpectedTypeMissmatchSemanticException(cast(data), expectedType, typeReturnType);
            }
        }

        return expectedType;
    }

    @Override
    public Object visit(ASTBLOCK node, Object data) {
        translate("{ ");
        translateNewLine();
        IClosure<String, ComplexParserType, Object> ic = cast(data).createNewChildClosure("block");

        visitChildren(node, ic);
        translate("}");
        translateNewLine();
        return null;
    }

    // TODO check for correct assignment in case of others than =
    @Override
    public Object visit(ASTOP_PRIO_1 node, Object data) {
        translate(node.jjtGetValue() + " ");
        if (node.jjtGetNumChildren() == 1) return visit(node.jjtGetChild(0), data);

        // TODO is it even possible?
        throw new UnknownSemanticException(null);
    }

    @Override
    public Object visit(ASTOP_PRIO_3 node, Object data) {
        return inferOperatorFromTwoArguments(node, data);
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
        return inferOperatorFromTwoArguments(node, data);
    }

    @Override
    public Object visit(ASTOPERATOR_6 node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOP_PRIO_8 node, Object data) {
        return inferOperatorFromTwoArguments(node, data);
    }

    @Override
    public Object visit(ASTOPERATOR_8 node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOP_PRIO_9 node, Object data) {
        return inferOperatorFromTwoArguments(node, data);
    }

    @Override
    public Object visit(ASTOPERATOR_9 node, Object data) {
        return null;
    }

    public ComplexParserType inferOperatorFromTwoArguments(SimpleNode node, Object data) {

        ComplexParserType currentType = (ComplexParserType) visit(node.jjtGetChild(0), data);

        for (int i = 1; i < node.jjtGetNumChildren(); i += 2) {
            String operator = (String) ((SimpleNode) node.jjtGetChild(i)).jjtGetValue();

            translateDisabled = true;
            ComplexParserType cpt2 = (ComplexParserType) visit(node.jjtGetChild(i + 1), data);
            currentType = ComplexParserTypeIdentifier.inferDatatypeFromOperation(cast(data), currentType, cpt2, operator);
            translateDisabled = false;

            boolean treatSpecial = !currentType.isArray() && currentType.getBasicType() == ParserTypes.SET;

            translate(MessageFormat.format(treatSpecial ? ".overload(\"{0}\", " : " {0} ", operator));
            // REVISIT NODE FOR OUTPUT
            visit(node.jjtGetChild(i + 1), data);
            if (treatSpecial)
                translate(")");
        }

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
        return inferOperatorFromTwoArguments(node, data);
    }

    // TODO check for only variable increment y++ ... disallow 5++
    @Override
    public Object visit(ASTOP_PRIO_13 node, Object data) {
        String operator = (String) ((SimpleNode) node.jjtGetChild(0)).jjtGetValue();
        translate(operator);
        ComplexParserType cpt = (ComplexParserType) visit(node.jjtGetChild(1), data);

        return ComplexParserTypeIdentifier.inferDatatypeFromUnaryOperation(
                cast(data),
                cpt,
                operator,
                node);
    }

    @Override
    public Object visit(ASTOPERATOR_13 node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOP_PRIO_14 node, Object data) {
        ComplexParserType cpt = (ComplexParserType) visit(node.jjtGetChild(0), data);
        String operator = (String) ((SimpleNode) node.jjtGetChild(1)).jjtGetValue();

        translate(operator);

        return ComplexParserTypeIdentifier.inferDatatypeFromUnaryOperation(
                cast(data),
                cpt,
                operator,
                node);
    }

    @Override
    public Object visit(ASTOPERATOR_14 node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOP_PRIO_15 node, Object data) {
        ASTLITERAL_IDENTIFIER start = (ASTLITERAL_IDENTIFIER) node.jjtGetChild(0);
        String currentIdentifier = (String) start.jjtGetValue();
        IClosure<String, ComplexParserType, Object> currentClosure = cast(data);
        ComplexParserType currentType = null;

        for (int i = 1; i < node.jjtGetNumChildren(); i++) {
            SimpleNode current = (SimpleNode) node.jjtGetChild(i);
            SimpleNode next = i + 1 < node.jjtGetNumChildren() ? (SimpleNode) node.jjtGetChild(i + 1) : null;
            boolean isNextMethodCall = next instanceof ASTOPERATOR_15_METHOD_CALL;

            if (i == 1 && !(current instanceof ASTOPERATOR_15_METHOD_CALL)) {
                currentType = (ComplexParserType) visit(start, currentClosure);
                currentClosure = cast(currentClosure.getVariableTypeAndValue(currentIdentifier, false).getValue());
            }

            if (current instanceof ASTOPERATOR_15_MEMBER_SELECTOR ms) {
                translate(".");
                ASTLITERAL_IDENTIFIER ident = (ASTLITERAL_IDENTIFIER) ms.jjtGetChild(0);
                currentIdentifier = (String) ident.jjtGetValue();

                if (isNextMethodCall) {
                    continue;
                }

                currentType = (ComplexParserType) visit(ident, currentClosure);
                currentClosure = cast(currentClosure.getVariableTypeAndValue(currentIdentifier, false).getValue());
            }

            if (current instanceof ASTOPERATOR_15_METHOD_CALL mc) {
                if (isNextMethodCall)
                    throw new UnknownSemanticException("Es liegen zwei Methodenaufrufe aufeinander vor.");

                translate(currentIdentifier);
                ArrayList<ComplexParserType> givenParams = new ArrayList<>();
                translate("(");

                for (SimpleNode sn : childrenToArray(mc)) {
                    givenParams.addAll((Collection<? extends ComplexParserType>) visit(sn, cast(data)));
                }


                AbstractMap.SimpleEntry<ComplexParserType, IClosure<String, ComplexParserType, Object>> retrievedTypeClosure = currentClosure.getMethodTypeAndClosure(currentIdentifier, false);
                if (retrievedTypeClosure == null)
                    throw new MethodNotDeclaredSemanticException(cast(data), currentIdentifier);

                currentType = retrievedTypeClosure.getKey();
                currentClosure = retrievedTypeClosure.getValue();

                if (givenParams.size() != retrievedTypeClosure.getValue().getMethodParams().size())
                    throw new MethodParameterMismatchSemanticException(cast(currentClosure), currentIdentifier, new ArrayList<>(retrievedTypeClosure.getValue().getMethodParams().values()), givenParams);

                for (int j = 0; j < retrievedTypeClosure.getValue().getMethodParams().size(); j++)
                    if (!retrievedTypeClosure.getValue().getMethodParams().get(j).isEqual(givenParams.get(j)))
                        throw new MethodParameterMismatchSemanticException(cast(currentClosure), currentIdentifier, new ArrayList<>(retrievedTypeClosure.getValue().getMethodParams().values()), givenParams);

                translate(")");
            }

            if (current instanceof ASTOPERATOR_15_ARRAY_INDEX_CALL ai) {
                if (currentType == null)
                    throw new UnknownSemanticException(null);

                String closing;

                if ((currentType.getBasicType() == ParserTypes.MAP || currentType.getBasicType() == ParserTypes.SET) && !currentType.isArray()) {
                    translate(".get(");
                    closing = ")";
                } else {
                    translate("[");
                    closing = "]";
                }

                if (!currentType.isArray() && currentType.getBasicType() != ParserTypes.MAP && currentType.getBasicType() != ParserTypes.SET)
                    throw new NotArrayExceptionSemanticException(cast(currentClosure), currentIdentifier, currentType);

                ComplexParserType arraySelectorComplexType = (ComplexParserType) visit(ai.jjtGetChild(0), cast(data));

                if ((currentType.isArray() || currentType.getBasicType() == ParserTypes.SET) && !arraySelectorComplexType.isEqual(ParserTypes.INT) || currentType.getBasicType() == ParserTypes.MAP && !arraySelectorComplexType.isEqual(currentType.getComplexParserTypes().get(0)))
                    throw new ExpectedTypeMissmatchSemanticException(
                            cast(currentClosure),
                            currentType.getBasicType() == ParserTypes.MAP ? currentType.getComplexParserTypes().get(0) : new ComplexParserType(ParserTypes.INT),
                            arraySelectorComplexType);


                currentType = degradeArrayMapSet(currentType, false);
                currentClosure = retrieveClassClosure(currentType.getBasicType().toString());
                translate(closing);
            }
        }

        Main.logger.info(MessageFormat.format("[Operation 15] Call to {0} resulted in type: {1}", ((ASTLITERAL_IDENTIFIER) node.jjtGetChild(0)).jjtGetValue(), currentType));
        return currentType;
    }

    @Override
    public Object visit(ASTOPERATOR_15_METHOD_CALL node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOPERATOR_15_METHOD_CALL_PARAMS node, Object data) {
        ArrayList<ComplexParserType> arr = new ArrayList<>();

        boolean first = true;
        for (SimpleNode sn : childrenToArray(node)) {
            if (!first)
                translate(", ");
            arr.add((ComplexParserType) visit(sn, data));
            first = false;
        }

        return arr;
    }

    @Override
    public Object visit(ASTOPERATOR_15_METHOD_CALL_PARAM node, Object data) {
        return visit(node.jjtGetChild(0), data);
    }

    @Override
    public Object visit(ASTOPERATOR_15_MEMBER_SELECTOR node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOPERATOR_15_ARRAY_INDEX_CALL node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTLITERAL_IDENTIFIER node, Object data) {
        translate(node.jjtGetValue());
        AbstractMap.SimpleEntry<ComplexParserType, Object> s = cast(data).getVariableTypeAndValue((String) node.jjtGetValue(), false);

        if (s.getValue() == null)
            throw new VariableNotInitializedSemanticException(cast(data), (String) node.jjtGetValue());

        return s.getKey();
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

        for (SimpleNode sn : childrenToArray(node))
            visit(sn, cpt);

        return cpt;
    }

    @Override
    public Object visit(ASTARRAY_CONTAINER_NATIVE node, Object data) {
        SimpleNode parent = (SimpleNode) node.jjtGetParent();
        ArrayList<Integer> traverseTypeSpecifiers = new ArrayList<>();

        while (!(parent instanceof ASTVAR_INIT)) {
            if (parent instanceof ASTMAP_ELEMENT_VALUE)
                traverseTypeSpecifiers.add(1);

            if (parent instanceof ASTMAP_ELEMENT_KEY || parent instanceof ASTARRAY_ELEMENT)
                traverseTypeSpecifiers.add(0);

            parent = (SimpleNode) parent.jjtGetParent();
        }

        ASTLITERAL_IDENTIFIER init_ident = (ASTLITERAL_IDENTIFIER) parent.jjtGetChild(0);
        ComplexParserType arrayHolderType = cast(data).getVariableTypeAndValue(init_ident.jjtGetValue().toString(), false).getKey();

        for (int i = traverseTypeSpecifiers.size() - 1; i >= 0; i--) {
            arrayHolderType = arrayHolderType.clone();
            arrayHolderType = arrayHolderType.getComplexParserTypes().get(traverseTypeSpecifiers.get(i));
        }

        if (!arrayHolderType.isArray())
            throw new WrongArrayDefinitionSemanticException(
                    cast(data),
                    MessageFormat.format(
                            "Das Objekt {0} ist vom Datentyp {1} und ist keine Array. Eine Array wird mit '{'...'}' definiert, wobei Set und Map mit [...] definiert werden."
                            , init_ident.jjtGetValue(), arrayHolderType.toString()));

        translate("new " + arrayHolderType.toStringJava(traverseTypeSpecifiers.isEmpty()) + " {", false);

        if (node.jjtGetNumChildren() == 0) {
            translate("}", false);
            return new ComplexParserType(ParserTypes.EMPTY_ARRAY_CONTAINER);
        }

        ArrayList<Object> cpt = new ArrayList<>();

        boolean first = true;
        for (SimpleNode sn : childrenToArray(node)) {
            if (!first)
                translate(", ");
            cpt.add(visit(sn, data));

            first = false;
        }

        translate("}");

        return cpt;
    }

    @Override
    public Object visit(ASTARRAY_CONTAINER node, Object data) {
        SimpleNode parent = (SimpleNode) node.jjtGetParent();
        ArrayList<Integer> traverseTypeSpecifiers = new ArrayList<>();

        while (!(parent instanceof ASTVAR_INIT)) {
            if (parent instanceof ASTMAP_ELEMENT_VALUE)
                traverseTypeSpecifiers.add(1);

            if (parent instanceof ASTMAP_ELEMENT_KEY || parent instanceof ASTARRAY_ELEMENT)
                traverseTypeSpecifiers.add(0);

            parent = (SimpleNode) parent.jjtGetParent();
        }

        ASTLITERAL_IDENTIFIER init_ident = (ASTLITERAL_IDENTIFIER) parent.jjtGetChild(0);
        ComplexParserType arrayHolderType = cast(data).getVariableTypeAndValue(init_ident.jjtGetValue().toString(), false).getKey();

        for (int i = traverseTypeSpecifiers.size() - 1; i >= 0; i--) {
            arrayHolderType = arrayHolderType.clone();
            arrayHolderType = arrayHolderType.getComplexParserTypes().get(traverseTypeSpecifiers.get(i));
        }

        if (arrayHolderType.isArray())
            throw new WrongArrayDefinitionSemanticException(
                    cast(data),
                    MessageFormat.format(
                            "Das Objekt {0} ist vom Datentyp {1} und ist eine Array. Eine Array wird mit '{'...'}' definiert, wobei Set und Map mit [...] definiert werden."
                            , init_ident.jjtGetValue(), arrayHolderType.toString()));

        translate("new " + arrayHolderType.toStringJava(true) + "()");

        if (node.jjtGetNumChildren() == 0) {
            return new ComplexParserType(ParserTypes.EMPTY_ARRAY_CONTAINER);
        }

        ArrayList<Object> cpt = new ArrayList<>();
        return getArrayElementChildrenWithSeparators(data, cpt, childrenToArray(node), arrayHolderType.getBasicType() == ParserTypes.MAP ? ".put(" : ".add(");
    }

    @Override
    public Object visit(ASTARRAY_ELEMENT node, Object data) {
        return visit(node.jjtGetChild(0), data);
    }

    @Override
    public Object visit(ASTMAP_ELEMENT node, Object data) {
        ArrayList<Object> cpt = new ArrayList<>();

        return getArrayElementChildrenWithSeparators(data, cpt, childrenToArray(node), null);
    }

    private Object getArrayElementChildrenWithSeparators(Object data, ArrayList<Object> cpt, ArrayList<SimpleNode> simpleNodes, String opening) {
        boolean first = true;
        for (SimpleNode sn : simpleNodes) {
            if (opening == null) {
                if (!first)
                    translate(", ");
            } else
                translate(opening);
            cpt.add(visit(sn, data));
            if (opening != null)
                translate(")");
            first = false;
        }

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
        translate(node.jjtGetValue());
        return new ComplexParserType(ParserTypes.INT);
    }

    @Override
    public Object visit(ASTLITERAL_BOOLEAN node, Object data) {
        translate(node.jjtGetValue());
        return new ComplexParserType(ParserTypes.BOOLEAN);
    }

    @Override
    public Object visit(ASTLITERAL_CHAR node, Object data) {
        translate(node.jjtGetValue());
        return new ComplexParserType(ParserTypes.CHAR);
    }

    @Override
    public Object visit(ASTLITERAL_STRING node, Object data) {
        translate(node.jjtGetValue());
        return new ComplexParserType(ParserTypes.STRING);
    }

    @Override
    public Object visit(ASTLITERAL_PATH node, Object data) {
        translate("new Path(\"" + node.jjtGetValue().toString().replaceAll("\\\\", "\\\\\\\\") + "\")");
        return new ComplexParserType(ParserTypes.PATH);
    }
}
