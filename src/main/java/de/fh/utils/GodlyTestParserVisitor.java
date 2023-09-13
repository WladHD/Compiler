package de.fh.utils;

import de.fh.javacc.generated.*;

public interface GodlyTestParserVisitor extends TestParserVisitor {
    @Override
    default Object visit(SimpleNode node, Object data) {
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
        else if (node instanceof ASTATOM_ARRAY a)
            return visit(a, a.jjtGetValue());
        else if (node instanceof ASTATOM_STRING a)
            return visit(a, a.jjtGetValue());
        else if (node instanceof ASTARRAY_ELEMENT a)
            return visit(a, a.jjtGetValue());


        return "/* Konnte " + node.getClass().getSimpleName() + " nicht parsen */";
    }
}
