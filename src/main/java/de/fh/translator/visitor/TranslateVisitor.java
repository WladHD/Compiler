package de.fh.translator.visitor;

import de.fh.javacc.generated.*;
import org.checkerframework.checker.units.qual.A;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class TranslateVisitor implements TestParserVisitor {

    private String childrenToText(SimpleNode root) {
        StringBuilder carry = new StringBuilder();

        if (root.jjtGetNumChildren() != 0) {
            for (int i = 0; i < root.jjtGetNumChildren(); ++i) {
                SimpleNode n = (SimpleNode) root.jjtGetChild(i);
                if (n != null) {
                    Object val = visit(n, n.jjtGetValue());
                    carry.append(val == null ? "/* ?" + n.getClass().getSimpleName() + "? */" : val);

                    if(i + 1 < root.jjtGetNumChildren())
                        carry.append(" ");
                }
            }
        }

        return carry.toString();
    }

    private ArrayList<SimpleNode> childrenToArray(SimpleNode root) {
        ArrayList<SimpleNode> sn = new ArrayList<>();

        if (root.jjtGetNumChildren() != 0) {
            for (int i = 0; i < root.jjtGetNumChildren(); ++i) {
                SimpleNode n = (SimpleNode) root.jjtGetChild(i);
                if (n != null)
                    sn.add(n);
            }
        }

        return sn;
    }

    @Override
    public Object visit(SimpleNode node, Object data) {
        if (node instanceof ASTPROGRAM a)
            return visit(a, a.jjtGetValue());
        else if (node instanceof ASTREQUESTER_VAR_METHOD a)
            return visit(a, a.jjtGetValue());
        else if (node instanceof ASTGET_VAR_METHOD a)
            return visit(a, a.jjtGetValue());
        else if (node instanceof ASTGET_METHOD_PARAMS a)
            return visit(a, a.jjtGetValue());
        else if (node instanceof ASTPARAM a)
            return visit(a, a.jjtGetValue());
        else if (node instanceof ASTSTATEMENT a)
            return visit(a, a.jjtGetValue());
        else if (node instanceof ASTDECL a)
            return visit(a, a.jjtGetValue());
        else if (node instanceof ASTMETHDECL a)
            return visit(a, a.jjtGetValue());
        else if (node instanceof ASTPARAM_PAIR a)
            return visit(a, a.jjtGetValue());
        else if (node instanceof ASTTYPE a)
            return visit(a, a.jjtGetValue());
        else if (node instanceof ASTKOMPLEX_TYPE a)
            return visit(a, a.jjtGetValue());
        else if (node instanceof ASTVAR_METHOD_NAME a)
            return visit(a, a.jjtGetValue());
        else if (node instanceof ASTBLOCK a)
            return visit(a, a.jjtGetValue());
        else if (node instanceof ASTWHILE a)
            return visit(a, a.jjtGetValue());
        else if (node instanceof ASTCONDITION a)
            return visit(a, a.jjtGetValue());
        else if (node instanceof ASTVARIABLE_ASSIGNMENT_PRIO_1 a)
            return visit(a, a.jjtGetValue());
        else if (node instanceof ASTOPERATION_PRIO_9 a)
            return visit(a, a.jjtGetValue());
        else if (node instanceof ASTOPERATION_PRIO_4_AND_3 a)
            return visit(a, a.jjtGetValue());
        else if (node instanceof ASTOPERATION_PRIO_11 a)
            return visit(a, a.jjtGetValue());
        else if (node instanceof ASTOPERATION_PRIO_12 a)
            return visit(a, a.jjtGetValue());
        else if (node instanceof ASTOPERATION_PRIO_13 a)
            return visit(a, a.jjtGetValue());
        else if (node instanceof ASTOPERATION_PRIO_14 a)
            return visit(a, a.jjtGetValue());
        else if (node instanceof ASTATOM_VARIABLE a)
            return visit(a, a.jjtGetValue());
        else if (node instanceof ASTATOM_INT a)
            return visit(a, a.jjtGetValue());
        else if (node instanceof ASTATOM_DBL a)
            return visit(a, a.jjtGetValue());
        else if (node instanceof ASTATOM_BOL a)
            return visit(a, a.jjtGetValue());
        else if (node instanceof ASTCONCLUDED_VAR_DEC a)
            return visit(a, a.jjtGetValue());
        else if (node instanceof ASTDECL_METHOD_PARAMS a)
            return visit(a, a.jjtGetValue());


        return "/* Konnte SimpleNode nicht parsen */";
    }

    @Override
    public Object visit(ASTPROGRAM node, Object data) {
        String ph_Class = "public class Main '{' {0} '}'";
        return MessageFormat.format(ph_Class, childrenToText(node));
    }

    @Override
    public Object visit(ASTREQUESTER_VAR_METHOD node, Object data) {
        String ph = "{0}{1}";
        return MessageFormat.format(ph, data, childrenToText(node));
    }

    @Override
    public Object visit(ASTGET_VAR_METHOD node, Object data) {
        String ph = ".{0}{1}";
        return MessageFormat.format(ph, data, childrenToText(node));
    }

    @Override
    public Object visit(ASTGET_METHOD_PARAMS node, Object data) {
        String ph = "({0})";
        return MessageFormat.format(ph, childrenToText(node));
    }

    @Override
    public Object visit(ASTPARAM node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTSTATEMENT node, Object data) {
        // TODO WHILE Statement in andere Node einteilen
        String ph = "{0};";
        return MessageFormat.format(ph, childrenToText(node));
    }

    @Override
    public Object visit(ASTDECL node, Object data) {
        return childrenToText(node);
    }

    @Override
    public Object visit(ASTCONCLUDED_VAR_DEC node, Object data) {
        String ph_ConVar = "{0};";
        return MessageFormat.format(ph_ConVar, childrenToText(node));
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
    public Object visit(ASTTYPE node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTKOMPLEX_TYPE node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTVAR_METHOD_NAME node, Object data) {
        String children = childrenToText(node);
        String ph = "{0}" + (children.isEmpty() ? "" : " ") + "{1}";

        return MessageFormat.format(ph, data, children);
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
    public Object visit(ASTVARIABLE_ASSIGNMENT_PRIO_1 node, Object data) {
        String ph_ConVar = "{0} {1}";
        return MessageFormat.format(ph_ConVar, data, childrenToText(node));
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
        String ph_ConVar = "{0} {1}";
        return MessageFormat.format(ph_ConVar, data, childrenToText(node));
    }

    @Override
    public Object visit(ASTOPERATION_PRIO_12 node, Object data) {
        String ph_ConVar = "{0} {1}";
        return MessageFormat.format(ph_ConVar, data, childrenToText(node));
    }

    @Override
    public Object visit(ASTOPERATION_PRIO_13 node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTOPERATION_PRIO_14 node, Object data) {
        String ph_ConVar = "{0}{1}";
        return MessageFormat.format(ph_ConVar, childrenToText(node), data);
    }

    @Override
    public Object visit(ASTATOM_VARIABLE node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTATOM_INT node, Object data) {
        return node.jjtGetValue();
    }

    @Override
    public Object visit(ASTATOM_DBL node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTATOM_BOL node, Object data) {
        String ph_ConVar = "({0})";
        return MessageFormat.format(ph_ConVar, childrenToText(node));
    }
}
