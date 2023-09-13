package de.fh.semantic;

import de.fh.javacc.generated.*;
import de.fh.semantic.closure.Closure;
import de.fh.utils.GodlyTestParserVisitor;


public class TreeTraverse implements GodlyTestParserVisitor {

    private final Closure currentClosure;

    public TreeTraverse(Closure currentClosure) {
        this.currentClosure = currentClosure;
    }

    @Override
    public Object visit(ASTPROGRAM node, Object data) {

        return null;
    }

    @Override
    public Object visit(ASTREQUESTER_VAR_METHOD node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTGET_VAR_METHOD node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTGET_METHOD_PARAMS node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTPARAM node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTSTATEMENT node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTDECL node, Object data) {

        String type = ((SimpleNode) node.jjtGetChild(0)).jjtGetValue().toString();
        String name = ((SimpleNode) node.jjtGetChild(1)).jjtGetValue().toString();

        Object test = this.visit((SimpleNode) node.jjtGetChild(0), null);
        System.out.println(test);
        System.out.println("test");
        Object tes2 = this.visit((SimpleNode) node.jjtGetChild(1), null);
        System.out.println(tes2);
        System.out.println(node.jjtGetNumChildren());
        currentClosure.addBoundVariable(name, type);
        return null;
    }

    @Override
    public Object visit(ASTTYPE node, Object data) {
        return node.jjtGetValue();
    }

    @Override
    public Object visit(ASTCONCLUDED_VAR_DEC node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTATOM_PATH node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMETHDECL node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTDECL_METHOD_PARAMS node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTPARAM_PAIR node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTKOMPLEX_TYPE node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTVAR_METHOD_NAME node, Object data) {
        return node.jjtGetValue();
    }

    @Override
    public Object visit(ASTBLOCK node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTWHILE node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTCONDITION node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTFor node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTATOM_ARRAY node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTATOM_ELEMENT node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTARRAY_ELEMENT node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTVARIABLE_ASSIGNMENT_PRIO_1 node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOPERATION_PRIO_4_AND_3 node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOPERATION_PRIO_9 node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOPERATION_PRIO_11 node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOPERATION_PRIO_12 node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOPERATION_PRIO_13 node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOPERATION_PRIO_14 node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTATOM_VARIABLE node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTATOM_INT node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTATOM_DBL node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTATOM_BOL node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTATOM_STRING node, Object data) {
        return null;
    }
}
