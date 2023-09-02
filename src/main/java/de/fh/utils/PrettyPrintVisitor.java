package de.fh.utils;

import de.fh.javacc.generated.SimpleNode;

public class PrettyPrintVisitor {

    public static void dump(SimpleNode root) {
        dump(root, "");
    }

    public static void dump(SimpleNode root, String prefix) {
        System.out.println(root.toString(prefix) + " = " + root.jjtGetValue());
        if (root.jjtGetNumChildren() != 0) {
            for (int i = 0; i < root.jjtGetNumChildren(); ++i) {
                SimpleNode n = (SimpleNode) root.jjtGetChild(i);
                if (n != null) {
                    dump(n, prefix + " ");
                }
            }
        }
    }
}
