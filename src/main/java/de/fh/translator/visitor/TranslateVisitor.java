package de.fh.translator.visitor;

import de.fh.javacc.generated.*;
import de.fh.utils.ClassLoaderReader;
import de.fh.utils.GodlyTestParserVisitor;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class TranslateVisitor implements GodlyTestParserVisitor {

    public final static String LINE_SEPARATOR = "\n\t   ";
    private boolean fancyFlag = false;


    private String childrenToText(SimpleNode root) {
        return childrenToText(root, " ");
    }

    private String childrenToText(SimpleNode root, String sep) {
        StringBuilder carry = new StringBuilder();

        if (root.jjtGetNumChildren() != 0) {
            for (int i = 0; i < root.jjtGetNumChildren(); ++i) {
                SimpleNode n = (SimpleNode) root.jjtGetChild(i);
                if (n != null) {
                    Object val = visit(n, n.jjtGetValue());

                    if (fancyFlag) {
                        fancyFlag = false;
                        return (String) val;
                    }

                    carry.append(val == null ? "/* ?" + n.getClass().getSimpleName() + "? */" : val);

                    if (i + 1 < root.jjtGetNumChildren()) carry.append(sep);
                }
            }
        }

        return carry.toString();
    }

    @Override
    public Object visit(ASTPROGRAM node, Object data) {
        String s = ClassLoaderReader.getResourceFileAsString("TranslatorTemplate.java");

        if (s == null) throw new RuntimeException("Konnte Template zur Java Source Code Erzeugung nicht finden ...");

        return s.replace("/* PLACEHOLDER */", childrenToText(node));
    }

    @Override
    public Object visit(ASTREQUESTER_VAR_METHOD node, Object data) {
        String ph = "{0}{1}";
        return MessageFormat.format(ph, data, childrenToText(node));
    }

    @Override
    public Object visit(ASTGET_VAR_METHOD node, Object data) {
        String ph = ".{0}{1}";
        return MessageFormat.format(ph, data, childrenToText(node, ""));
    }

    @Override
    public Object visit(ASTGET_METHOD_PARAMS node, Object data) {
        String ph = "({0})";
        return MessageFormat.format(ph, childrenToText(node, ", "));
    }

    @Override
    public Object visit(ASTPARAM node, Object data) {
        return childrenToText(node);
    }

    @Override
    public Object visit(ASTSTATEMENT node, Object data) {
        // TODO WHILE Statement in andere Node einteilen
        String ph = "{0};{1}";
        return MessageFormat.format(ph, childrenToText(node), LINE_SEPARATOR);
    }

    @Override
    public Object visit(ASTDECL node, Object data) {
        return childrenToText(node) + LINE_SEPARATOR;
    }

    @Override
    public Object visit(ASTCONCLUDED_VAR_DEC node, Object data) {
        String ph_ConVar = "{0};";
        return MessageFormat.format(ph_ConVar, childrenToText(node));
    }

    @Override
    public Object visit(ASTMETHDECL node, Object data) {
        return childrenToText(node);
    }

    @Override
    public Object visit(ASTDECL_METHOD_PARAMS node, Object data) {
        String ph_ConVar = "({0})";
        return MessageFormat.format(ph_ConVar, childrenToText(node, ", "));
    }

    @Override
    public Object visit(ASTPARAM_PAIR node, Object data) {
        return childrenToText(node);
    }

    @Override
    public Object visit(ASTTYPE node, Object data) {
        String ph = "{0}{1}";
        return MessageFormat.format(ph, data, childrenToText(node));
    }

    @Override
    public Object visit(ASTKOMPLEX_TYPE node, Object data) {
        String ph = "<{0}>";
        return MessageFormat.format(ph, childrenToText(node));
    }

    @Override
    public Object visit(ASTVAR_METHOD_NAME node, Object data) {
        String children = childrenToText(node);
        String ph = "{0}" + (children.isEmpty() ? "" : " ") + "{1}";

        return MessageFormat.format(ph, data, children);
    }

    @Override
    public Object visit(ASTBLOCK node, Object data) {
        String ph = "'{'{1} {0} '}'";
        return MessageFormat.format(ph, childrenToText(node), LINE_SEPARATOR);
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
        String ph = "'{' {0} '}'";
        return MessageFormat.format(ph, childrenToText(node, ", "));
    }

    public Object visit(ASTATOM_PATH node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTARRAY_ELEMENT node, Object data) {
        return childrenToText(node);
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
        System.out.println(node.jjtGetParent());
        System.out.println(node.jjtGetNumChildren());

        ArrayList<SimpleNode> sn = childrenToArray((SimpleNode) node.jjtGetParent());

        System.out.println(Arrays.toString(sn.toArray()));

        ArrayList<Object> current = new ArrayList<>();
        current.add(visit(sn.get(0), null));

        for (int i = 1; i < sn.size(); i++) {
            if (sn.get(i) instanceof ASTOPERATION_PRIO_11 a) {
                current.add(0, a.jjtGetValue());

                current.add(2, (a.jjtGetNumChildren() == 1 ? visit((SimpleNode) a.jjtGetChild(0), null) : childrenToText(a)));


                ArrayList<Object> temp = new ArrayList<>();
                String template = "OpOverload.p11(''{0}'', {1}, {2})";
                temp.add(MessageFormat.format(template, current.get(0), current.get(1), current.get(2)));

                current = temp;

            } else {
                current.set(0, current.get(0).toString() + " " + visit(sn.get(i), null).toString());
            }
        }

        fancyFlag = true;

        System.out.println(current.get(0));


        return current.get(0);
    }

    @Override
    public Object visit(ASTOPERATION_PRIO_12 node, Object data) {
        String ph_ConVar = "{0} {1}";
        return MessageFormat.format(ph_ConVar, data, childrenToText(node));
    }

    @Override
    public Object visit(ASTOPERATION_PRIO_13 node, Object data) {
        String ph_ConVar = "{0}{1}";
        return MessageFormat.format(ph_ConVar, data, childrenToText(node));
    }

    @Override
    public Object visit(ASTOPERATION_PRIO_14 node, Object data) {
        String ph_ConVar = "{0}{1}";
        return MessageFormat.format(ph_ConVar, childrenToText(node), data);
    }

    @Override
    public Object visit(ASTATOM_VARIABLE node, Object data) {
        return data;
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
        return data;
    }

    @Override
    public Object visit(ASTATOM_STRING node, Object data) {
        return null;
    }
}
