package de.fh.utils;

import de.fh.javacc.generated.*;
import de.fh.semantic.closure.IClosure;
import de.fh.semantic.err.SemanticException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public interface GodlyTestParserVisitor extends TestParserVisitor {

    default ArrayList<SimpleNode> childrenToArray(SimpleNode root) {
        ArrayList<SimpleNode> sn = new ArrayList<>();

        if (root.jjtGetNumChildren() != 0) {
            for (int i = 0; i < root.jjtGetNumChildren(); ++i) {
                SimpleNode n = (SimpleNode) root.jjtGetChild(i);
                if (n != null) sn.add(n);
            }
        }

        return sn;
    }

    default Object visit(Node n, Object data) {
        return visit((SimpleNode) n, data);
    }

    default void visitAll(SimpleNode root) {
        visitAll(root, root.jjtGetValue());
    }

    default void visitAll(SimpleNode root, Object obj) {
        if (root.jjtGetNumChildren() != 0) {
            for (int i = 0; i < root.jjtGetNumChildren(); ++i) {
                SimpleNode sn = (SimpleNode) root.jjtGetChild(i);
                visit(sn, obj);
            }
        }
    }

    @Override
    default Object visit(SimpleNode node, Object data) {
        for (Method m : this.getClass().getMethods()) {
            if(m.getParameterCount() != 2 || !m.getName().equals("visit") || m.getParameterTypes()[0] == SimpleNode.class || m.getParameterTypes()[0] != node.getClass())
                continue;

            try {
                return m.invoke(this, node, data);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e.getCause());
            }
        }


        return "/* Konnte " + node.getClass().getSimpleName() + " nicht parsen */";
    }
}
