package de.fh.semantic;

import de.fh.javacc.generated.*;
import de.fh.semantic.closure.Closure;
import de.fh.utils.GodlyTestParserVisitor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class TreeTraverse implements GodlyTestParserVisitor {

    private final Closure currentClosure;

    public TreeTraverse(Closure currentClosure) {
        this.currentClosure = currentClosure;
    }

    @Override
    public Object visit(ASTPROGRAM node, Object data) {
        String errorMessage = "";
        if (node.jjtGetNumChildren() != 0) {
            for (int i = 0; i < node.jjtGetNumChildren(); ++i) {
                SimpleNode sn = (SimpleNode) node.jjtGetChild(i);
                errorMessage = this.visit(sn, sn.jjtGetValue()).toString();
                if (!(errorMessage.equals(""))){
                    break;
                }
            }
        }
        return errorMessage;
    }

    @Override
    public Object visit(ASTREQUESTER_VAR_METHOD node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTARRAYAUFRUF node, Object data) {
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
        boolean itWorked = true;
        String errorMessage = "";
        Object type = this.visit((SimpleNode) node.jjtGetChild(0), null);

        Object name = this.visit((SimpleNode) node.jjtGetChild(1), null);

        SimpleNode concludeMeth = (SimpleNode) node.jjtGetChild(2);

        if (concludeMeth.toString().equals("METHDECL")){

            itWorked = currentClosure.addBoundMethod(name.toString(), type.toString(), currentClosure.createNewClosure());
            // open tree for meth declaration
            Object value = this.visit((SimpleNode) concludeMeth.jjtGetChild(0), name);
            if(!itWorked){
                return "The Methode already exists";
            }

        } else if(concludeMeth.toString().equals("CONCLUDED_VAR_DEC")){

            itWorked = currentClosure.addBoundVariable(name.toString(), type.toString());
            // open tree for var declare if value exists

            if(!itWorked){
                return "the Variable already exists";
            }
            if (concludeMeth.jjtGetNumChildren() == 1){
                // access VARIABLE_ASSIGNMENT_PRIO_1
                Object value = this.visit((SimpleNode) concludeMeth.jjtGetChild(0), type);
                if (value.toString().equals("")){


                    itWorked = currentClosure.addBoundVariableValue(name.toString(), true);

                    if (!itWorked){

                        return "The variable "+ name.toString()+ " already has a value";

                    }


                }else {
                    return value.toString();
                }
            }

        } else {
            return "Unknown token " + concludeMeth.toString();
        }

        // add a error if it didnt work

        return "";
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


        return true;
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
    public Object visit(ASTRETURN node, Object data) {
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
    public Object visit(ASTFOREACH node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTIF node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTELSE node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTFORINIT node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTFORCOND node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTFOROPER node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTATOM_ARRAY node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTATOM_SET node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTATOM_MAP node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTATOM_PATHELEMENT node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMAP_PAIR node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMAP_KEY node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMAP_VALUE node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTARRAY_ELEMENT node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTSET_ELEMENT node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTVARIABLE_ASSIGNMENT_PRIO_1 node, Object data) {
        Object returnValue = null;
        Set<String> validValues = new HashSet<>(Arrays.asList("int", "String", "char", "boolean"));

        for(int children = 0; children < node.jjtGetNumChildren(); children++){
            returnValue = this.visit((SimpleNode) node.jjtGetChild(children), data);
            if (!returnValue.toString().equals("") && !validValues.contains(returnValue.toString())){
                return returnValue.toString();
            } else if (!returnValue.toString().equals("") && !data.toString().equals(returnValue.toString())){
                return "you tried to assign " + returnValue.toString() + " to "+ data.toString();
            }
        }

        return "";
    }

    @Override
    // <BinJunktor : "||" | "&&">
    public Object visit(ASTOPERATION_PRIO_4_AND_3 node, Object data) {

        Set<String> validValues = new HashSet<>(Arrays.asList("boolean"));

        if (validValues.contains(data.toString())) {
            Object returnValue = this.visit((SimpleNode) node.jjtGetChild(0), data);
            if (data.toString().equals(returnValue.toString()) || returnValue.equals("")) {
                return "";
            } else {
                return "You tried to assign " + data.toString() + " to "+ returnValue.toString();
            }

        } else {
            return "You can't assign " + data.toString() + " this operator";
        }
    }

    @Override
    // <BinVergleich : ">="  | "<=" | "==" | "!=" > | <LessThan: "<"> | <GreaterThan: ">">
    public Object visit(ASTOPERATION_PRIO_9 node, Object data) {
        Set<String> validValues = new HashSet<>(Arrays.asList("int", "boolean"));

        if (validValues.contains(data.toString())) {
            Object returnValue = this.visit((SimpleNode) node.jjtGetChild(0), data);
            if (data.toString().equals(returnValue.toString()) || returnValue.equals("")) {
                return "";
            } else {
                return "You tried to assign " + data.toString() + " to "+ returnValue.toString();
            }

        } else {
            return "You can't assign " + data.toString() + " this operator";
        }

    }

    @Override
    // <OpSum : "+" | "-" | "^" >
    public Object visit(ASTOPERATION_PRIO_11 node, Object data) {

        Set<String> validValues = new HashSet<>(Arrays.asList("int", "String", "char"));

        if (validValues.contains(data.toString())) {
            Object returnValue = this.visit((SimpleNode) node.jjtGetChild(0), data);
            if (data.toString().equals(returnValue.toString()) || returnValue.equals("")) {
                return "";
            } else {
                return "You tried to assign " + data.toString() + " to "+ returnValue.toString();
            }

        } else {
            return "You can't assign " + data.toString() + " this operator";
        }
    }

    @Override
    // <OpProd : "*" | "%"> |  <FWD: "/">
    public Object visit(ASTOPERATION_PRIO_12 node, Object data) {

        Set<String> validValues = new HashSet<>(Arrays.asList("int"));

        if (validValues.contains(data.toString())) {
            Object returnValue = this.visit((SimpleNode) node.jjtGetChild(0), data);
            if (data.toString().equals(returnValue.toString()) || returnValue.equals("")) {
                return "";
            } else {
                return "You tried to assign " + data.toString() + " to "+ returnValue.toString();
            }

        } else {
            return "You can't assign " + data.toString() + " this operator";
        }
    }

    @Override
    // vorzeichen
    // <OpSum : "+" | "-" | "^" > |  <OpUnaer: "!"> |  <OpIncrement: "++" | "--">
    public Object visit(ASTOPERATION_PRIO_13 node, Object data) {
        Set<String> validValues = new HashSet<>(Arrays.asList("int"));

        if (validValues.contains(data.toString())) {
            Object returnValue = this.visit((SimpleNode) node.jjtGetChild(0), data);
            if (data.toString().equals(returnValue.toString()) || returnValue.equals("")) {
                return "";
            } else {
                return "You tried to assign " + data.toString() + " to "+ returnValue.toString();
            }

        } else {
            return "You can't assign " + data.toString() + " this operator";
        }
    }

    @Override
    // <OpIncrement: "++" | "--">
    public Object visit(ASTOPERATION_PRIO_14 node, Object data) {

        Set<String> validValues = new HashSet<>(Arrays.asList("int"));

        if (validValues.contains(data.toString())) {
            Object returnValue = this.visit((SimpleNode) node.jjtGetChild(0), data);
            if (data.toString().equals(returnValue.toString()) || returnValue.equals("")) {
                return "";
            } else {
                return "You tried to assign " + data.toString() + " to "+ returnValue.toString();
            }

        } else {
            return "You can't assign " + data.toString() + " this operator";
        }
    }

    @Override
    public Object visit(ASTATOM_VARIABLE node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTATOM_INT node, Object data) {
        return "int";
    }

    @Override
    public Object visit(ASTATOM_BOL node, Object data) {
        return "boolean";
    }

    @Override
    public Object visit(ASTATOM_STRING node, Object data) {
        return "String";
    }

    @Override
    public Object visit(ASTATOM_CHAR node, Object data) {
        return "char";
    }
    public Closure getClosure () {
        return this.currentClosure;
    }
}
