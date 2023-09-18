package de.fh.translator.visitor;

import de.fh.javacc.generated.*;
import de.fh.utils.ClassLoaderReader;
import de.fh.utils.GodlyTestParserVisitor;

import java.text.MessageFormat;

public class TranslateVisitor implements GodlyTestParserVisitor {

    public final static String LINE_SEPARATOR = "\n\t   ";


    private String childrenToText(SimpleNode root) {
        return childrenToText(root, " ");
    }

    private String childrenToText(SimpleNode root, String sep) {
        StringBuilder carry = new StringBuilder();

        if (root.jjtGetNumChildren() != 0) {
            for (int i = 0; i < root.jjtGetNumChildren(); ++i) {
                SimpleNode n = (SimpleNode) root.jjtGetChild(i);
                if (n != null) {
                    Object val = visit(n, null);

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
        return MessageFormat.format(ph, node.jjtGetValue(), childrenToText(node));
    }

    @Override
    public Object visit(ASTARRAYAUFRUF node, Object data) {
        String ph = "[{0}]";
        return MessageFormat.format(ph, childrenToText(node));
    }

    @Override
    public Object visit(ASTGET_VAR_METHOD node, Object data) {
        String ph = "{0}{1}";
        return MessageFormat.format(ph, node.jjtGetValue(), childrenToText(node, ""));
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
        String ph = "{0}{2}{1}";
        return MessageFormat.format(ph, childrenToText(node), LINE_SEPARATOR, node.jjtGetValue());
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
        return MessageFormat.format(ph, node.jjtGetValue(), childrenToText(node));
    }

    @Override
    public Object visit(ASTKOMPLEX_TYPE node, Object data) {
        String ph = "<{0}>";
        return MessageFormat.format(ph, replacePrimTypes(childrenToText(node, ", ")));
    }

    @Override
    public Object visit(ASTTYPE_ARRAY node, Object data) {
        return "[]";
    }

    public String replacePrimTypes(String in) {
        return in.replaceAll("int", "Integer").replaceAll("boolean", "Boolean").replaceAll("double", "Double");
    }

    @Override
    public Object visit(ASTVAR_METHOD_NAME node, Object data) {
        String children = childrenToText(node);
        String ph = "{0}" + (children.isEmpty() ? "" : " ") + "{1}";

        return MessageFormat.format(ph, node.jjtGetValue(), children);
    }

    @Override
    public Object visit(ASTBLOCK node, Object data) {
        String ph = "'{'{1} {0} '}'";
        return MessageFormat.format(ph, childrenToText(node), LINE_SEPARATOR);
    }

    @Override
    public Object visit(ASTRETURN node, Object data) {
        String ph = "return{0};";
        String children = childrenToText(node);

        if (!children.isEmpty())
            children = " " + children;

        return MessageFormat.format(ph, children);
    }

    @Override
    public Object visit(ASTWHILE node, Object data) {
        String ph = "while ({0}) {1}";
        return MessageFormat.format(ph, visit((SimpleNode) node.jjtGetChild(0), null), visit((SimpleNode) node.jjtGetChild(1), null), LINE_SEPARATOR);
    }

    @Override
    public Object visit(ASTCONDITION node, Object data) {
        return childrenToText(node);
    }

    @Override
    public Object visit(ASTFOR node, Object data) {
        String ph = "for ({0}) {1}";
        return MessageFormat.format(ph, visit((SimpleNode) node.jjtGetChild(0), null), visit((SimpleNode) node.jjtGetChild(1), null), LINE_SEPARATOR);
    }

    @Override
    public Object visit(ASTFOR_BODY node, Object data) {
        return childrenToText(node);
    }

    @Override
    public Object visit(ASTFOR_BODY_NORMAL node, Object data) {
        return childrenToText(node, "; ");
    }

    @Override
    public Object visit(ASTFOR_BODY_EACH node, Object data) {
        return childrenToText(node, " : ");
    }

    @Override
    public Object visit(ASTIF node, Object data) {
        String ph = "if ({0}) {1}{2}";
        return MessageFormat.format(ph,
                visit((SimpleNode) node.jjtGetChild(0), null),
                visit((SimpleNode) node.jjtGetChild(1), null),
                node.jjtGetNumChildren() == 3 ? visit((SimpleNode) node.jjtGetChild(2), null) : "");

    }

    @Override
    public Object visit(ASTELSE node, Object data) {
        String ph = " else {0}";
        return MessageFormat.format(ph, childrenToText(node), LINE_SEPARATOR);

    }

    @Override
    public Object visit(ASTFORINIT node, Object data) {
        return childrenToText(node);
    }

    @Override
    public Object visit(ASTFORCOND node, Object data) {
        return childrenToText(node);
    }

    @Override
    public Object visit(ASTFOROPER node, Object data) {
        return childrenToText(node);
    }

    @Override
    public Object visit(ASTATOM_ARRAY node, Object data) {
        String ph = "'{' {0} '}'";
        return MessageFormat.format(ph, childrenToText(node, ", "));
    }

    @Override
    public Object visit(ASTATOM_SET node, Object data) {
        String ph = "new Set<>({0})";
        String children = childrenToText(node, ", ");

        return MessageFormat.format(ph, children);
    }

    @Override
    public Object visit(ASTATOM_MAP node, Object data) {
        String ph = "new Map<>({0})";
        return MessageFormat.format(ph, childrenToText(node, ", "));
    }

    @Override
    public Object visit(ASTATOM_PATHELEMENT node, Object data) {
        String ph = "{0}{1}";
        String children = childrenToText(node);
        String separator = java.io.File.separator;
        if(separator.equals("\\"))
            separator = "\\\\";

        if (!children.isEmpty())
            children = separator + children;

        return MessageFormat.format(ph, node.jjtGetValue(), children);
    }

    @Override
    public Object visit(ASTMAP_PAIR node, Object data) {
        String ph = "new Pair<>({0})";
        return MessageFormat.format(ph, childrenToText(node, ", "));
    }

    @Override
    public Object visit(ASTMAP_KEY node, Object data) {
        return childrenToText(node);
    }

    @Override
    public Object visit(ASTMAP_VALUE node, Object data) {
        return childrenToText(node);
    }

    public Object visit(ASTATOM_PATH node, Object data) {
        String ph = "= new Path(\"{0}\")";
        return MessageFormat.format(ph, childrenToText(node));
    }

    @Override
    public Object visit(ASTARRAY_ELEMENT node, Object data) {
        return childrenToText(node);
    }

    @Override
    public Object visit(ASTSET_ELEMENT node, Object data) {
        return childrenToText(node);
    }

    @Override
    public Object visit(ASTVARIABLE_ASSIGNMENT_PRIO_1 node, Object data) {
        String ph_ConVar = "{0} {1}";
        return MessageFormat.format(ph_ConVar, node.jjtGetValue(), childrenToText(node));
    }

    @Override
    public Object visit(ASTOPERATION_PRIO_4_AND_3 node, Object data) {
        String ph_ConVar = "{0} {1}";
        return MessageFormat.format(ph_ConVar, node.jjtGetValue(), childrenToText(node));
    }

    @Override
    public Object visit(ASTOPERATION_PRIO_9 node, Object data) {
        String ph_ConVar = "{0} {1}";
        return MessageFormat.format(ph_ConVar, node.jjtGetValue(), childrenToText(node));
    }

    @Override
    public Object visit(ASTOPERATION_PRIO_11 node, Object data) {
        String ph_ConVar = "{0} {1}";
        return MessageFormat.format(ph_ConVar, node.jjtGetValue(), childrenToText(node));
    }

    @Override
    public Object visit(ASTOPERATION_PRIO_12 node, Object data) {
        String ph_ConVar = "{0} {1}";
        return MessageFormat.format(ph_ConVar, node.jjtGetValue(), childrenToText(node));
    }

    @Override
    public Object visit(ASTOPERATION_PRIO_13 node, Object data) {
        String ph_ConVar = "{0}{1}";
        return MessageFormat.format(ph_ConVar, node.jjtGetValue(), childrenToText(node));
    }

    @Override
    public Object visit(ASTOPERATION_PRIO_14 node, Object data) {
        String ph_ConVar = "{0}{1}";
        return MessageFormat.format(ph_ConVar, childrenToText(node), node.jjtGetValue());
    }

    @Override
    public Object visit(ASTATOM_VARIABLE node, Object data) {
        return node.jjtGetValue();
    }

    @Override
    public Object visit(ASTATOM_INT node, Object data) {
        return node.jjtGetValue();
    }

    @Override
    public Object visit(ASTATOM_BOL node, Object data) {
        return node.jjtGetValue();
    }

    @Override
    public Object visit(ASTATOM_STRING node, Object data) {
        String ph_ConVar = "{0}";
        return MessageFormat.format(ph_ConVar, node.jjtGetValue());
    }

    @Override
    public Object visit(ASTATOM_CHAR node, Object data) {
        String ph_ConVar = "{0}";
        return MessageFormat.format(ph_ConVar, node.jjtGetValue());
    }
}
