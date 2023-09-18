package de.fh.semantic;

import de.fh.javacc.generated.*;
import de.fh.semantic.closure.Closure;
import de.fh.utils.GodlyTestParserVisitor;

import java.util.*;


public class TreeTraverse implements GodlyTestParserVisitor {

    private Closure currentClosure;

    public TreeTraverse(Closure currentClosure) {
        this.currentClosure = currentClosure;
    }

    @Override
    public Object visit(ASTPROGRAM node, Object data) {
        String errorMessage = "";
        if (node.jjtGetNumChildren() != 0) {
            for (int i = 0; i < node.jjtGetNumChildren(); ++i) {
                SimpleNode sn = (SimpleNode) node.jjtGetChild(i);
                errorMessage = this.visit(sn, null).toString();
                if (!(errorMessage.equals(""))) {
                    break;
                }
            }
        }
        return errorMessage;
    }

    @Override
    public Object visit(ASTREQUESTER_VAR_METHOD node, Object data) {
        return node.jjtGetValue().toString();
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
        Object name = this.visit((SimpleNode) node.jjtGetChild(0), data);
        Closure thisClosure = this.currentClosure;

        if (data != null) {
            thisClosure = (Closure) currentClosure.getChildClosureForMethod(data.toString());
        }
        String type = "";
        if (thisClosure.variableExists(name.toString()) || thisClosure.methodExists(name.toString())) {


            if (node.jjtGetChild(0).jjtGetNumChildren() > 0) {

                if (thisClosure.variableExists(name.toString())) {
                    type = thisClosure.getVariableTypeMap().get(name.toString());

                } else if (thisClosure.methodExists(name.toString())) {
                    type = thisClosure.getMethodReturnTypeMap().get(name.toString());

                }

            } else {
                return "Statement child had no Children";
            }

        }
        if (data != null && type.isEmpty()) {

            if (thisClosure.parameterExists(data.toString(), name.toString())) {
                if (node.jjtGetChild(0).jjtGetNumChildren() > 0) {
                    type = thisClosure.getParameterType(data.toString(), name.toString());
                } else {
                    return "Statement child had no Children";
                }
            }
        }
        if (thisClosure.getParent() != null && type.isEmpty()) {
            if (thisClosure.getParent().variableExists(name.toString())){
                if (node.jjtGetChild(0).jjtGetNumChildren() > 0) {
                    type = thisClosure.getParent().getVariableTypeMap().get(name.toString());
                } else {
                    return "Statement child had no Children";
                }

            } else if (name.toString().equals("BLOCK_FINISCHED")){
                name = "";
            } else {
                return this.visit((SimpleNode) node.jjtGetChild(0), data);
            }

        }

        if (!type.isEmpty()){

            Object returnValue = this.visit((SimpleNode) node.jjtGetChild(0).jjtGetChild(0), type);
            if (returnValue.toString().equals("")) {

                boolean itWorked = thisClosure.updateVariableValue(name.toString(), true);
                if (data != null){
                    if (thisClosure.parameterExists(data.toString(), name.toString()) && !itWorked) {

                        thisClosure.addBoundVariableValueWithoutCheck(name.toString(), true);
                        itWorked = true;
                    }
                }


                if (!itWorked) {
                    return "Variable couldn't be updated";
                }
                return "";

            } else {
                return returnValue.toString();
            }
        }  if (name.toString().equals("")){
            return "";
        }else {
            return "Can not resolve symbol: " + name.toString();
        }

    }



    @Override
    public Object visit(ASTDECL node, Object data) {
        boolean itWorked = true;
        String errorMessage = "";
        Closure thisClosure = this.currentClosure;

        if (data != null) {
            thisClosure = (Closure) currentClosure.getChildClosureForMethod(data.toString());

        }
        Object type = this.visit((SimpleNode) node.jjtGetChild(0), null);

        Object name = this.visit((SimpleNode) node.jjtGetChild(1), null);

        SimpleNode concludeMeth = (SimpleNode) node.jjtGetChild(2);

        if (concludeMeth.toString().equals("METHDECL")){

            itWorked = thisClosure.addBoundMethod(name.toString(), type.toString(), thisClosure.createNewClosure());
            //currentClosure.getChildClosureForMethod(name.toString()).getVariableTypeMap();
            if(!itWorked){
                return "The Methode already exists";
            }
            // open tree for meth declaration
            Object returnValue = this.visit(concludeMeth, name);
            if (!returnValue.toString().equals("")){
                return returnValue.toString();
            }

        } else if(concludeMeth.toString().equals("CONCLUDED_VAR_DEC")){

            itWorked = thisClosure.addBoundVariable(name.toString(), type.toString());
            // open tree for var declare if value exists

            if(!itWorked){
                return "The Variable already exists";
            }
            if (concludeMeth.jjtGetNumChildren() == 1){
                // access VARIABLE_ASSIGNMENT_PRIO_1
                Object value = this.visit((SimpleNode) concludeMeth.jjtGetChild(0), type);
                if (value.toString().equals("")){


                    itWorked = thisClosure.addBoundVariableValue(name.toString(), true);

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
        Object returnValue = null;
        for(int children = 0; children<node.jjtGetNumChildren(); children++){
            returnValue = this.visit((SimpleNode) node.jjtGetChild(children), data);
            if (!returnValue.toString().equals("")){
                return returnValue.toString();
            }
        }

        return "";
    }

    @Override
    public Object visit(ASTDECL_METHOD_PARAMS node, Object data) {
        Object returnValue = null;
        for(int children = 0; children<node.jjtGetNumChildren(); children++){
            returnValue = this.visit((SimpleNode) node.jjtGetChild(children), data);
            if (!returnValue.toString().equals("")){
                return returnValue.toString();
            }
        }

        return "";
    }

    @Override
    public Object visit(ASTPARAM_PAIR node, Object data) {
        if (currentClosure.methodExists(data.toString())){
            Object type = this.visit((SimpleNode) node.jjtGetChild(0), null);
            Object name = this.visit((SimpleNode) node.jjtGetChild(1), null);
            HashMap <String, String> methodParameters = new HashMap<>();
            methodParameters.put(name.toString(), type.toString());
            boolean itWorked = currentClosure.getChildClosureForMethod(data.toString()).addMethodParameters(data.toString(), methodParameters);
            if (!itWorked){
                return "The variable "+ name.toString() + " is already defined in this scope";
            }

            return "";
        } else {
            return "Method doesn't exist";
        }

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
        Object returnValue = null;
        for(int children = 0; children< node.jjtGetNumChildren(); children++){
            returnValue = this.visit((SimpleNode) node.jjtGetChild(children), data.toString());
            if (!returnValue.toString().equals("")){
                return returnValue.toString();
            }
        }
        return "BLOCK_FINISCHED";
    }

    @Override
    public Object visit(ASTRETURN node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTWHILE node, Object data) {
        // itWorked = thisClosure.addBoundMethod(name.toString(), type.toString(), thisClosure.createNewClosure());
        String name = "while";
        Set<String> validValues = new HashSet<>(Arrays.asList("int", "String", "char", "boolean"));
        Object returnValue = this.visit((SimpleNode) node.jjtGetChild(0),data);

        if (!returnValue.toString().equals("")){
            return  returnValue.toString();
        }
        boolean itWorked = false;
        int iteration = 0;

        while(!itWorked){
            iteration++;
            itWorked = currentClosure.addBoundMethod(name + iteration, "void", currentClosure.createNewClosure());
        }


        returnValue = this.visit((SimpleNode) node.jjtGetChild(1),name + iteration);
        if(!returnValue.toString().equals("")){
            return returnValue.toString();
        }


        return "";
    }

    @Override
    public Object visit(ASTCONDITION node, Object data) {
        String type = "";
        Set<String> validValues = new HashSet<>(Arrays.asList("int", "boolean"));
        Closure thisClosure = this.currentClosure;
        if (data != null) {
            thisClosure = (Closure) currentClosure.getChildClosureForMethod(data.toString());
        }
        boolean isBool = false;
        for(int children = 0; children<node.jjtGetNumChildren(); children++){
            Object returnValue = this.visit((SimpleNode) node.jjtGetChild(children),type);
            if(validValues.contains(returnValue.toString())){
                type = returnValue.toString();
                if (type.equals("int")){
                    isBool = isBoolStatement(node, data);
                    if (!isBool){
                        return "Condition was not a boolean";
                    }
                }
            } else if (thisClosure.variableExists(returnValue.toString())) {
                type = (thisClosure.getVariableTypeMap().get(returnValue.toString()));

            } else if (returnValue.toString().equals("")) {
                continue;
            } else {
                return returnValue.toString();
            }
        }

        return "";
    }

    @Override
    public Object visit(ASTFor node, Object data) {

        String name = "for";
        boolean itWorked = false;
        int iteration = 0;

        while(!itWorked){
            iteration++;
            itWorked = currentClosure.addBoundMethod(name + iteration, "void", currentClosure.createNewClosure());
        }

        for (int foriteration = 0; foriteration<3; foriteration++){
            Object returnValue = this.visit((SimpleNode) node.jjtGetChild(foriteration),name+iteration);

            if (!returnValue.toString().isEmpty()){
                return returnValue.toString();
            }
        }



        return "";
    }

    @Override
    public Object visit(ASTFOREACH node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTIF node, Object data) {

        String name = "if";
        Object returnValue = this.visit((SimpleNode) node.jjtGetChild(0),data);

        if (!returnValue.toString().equals("")){
            return  returnValue.toString();
        }
        boolean itWorked = false;
        int iteration = 0;

        while(!itWorked){
            iteration++;
            itWorked = currentClosure.addBoundMethod(name + iteration, "void", currentClosure.createNewClosure());
        }


        returnValue = this.visit((SimpleNode) node.jjtGetChild(1),name + iteration);
        if(!returnValue.toString().equals("")){
            return returnValue.toString();
        }


        return "";
    }

    @Override
    public Object visit(ASTELSE node, Object data) {

        return this.visit((SimpleNode) node.jjtGetChild(0), data);
    }

    @Override
    public Object visit(ASTFORINIT node, Object data) {
        Closure thisClosure = this.currentClosure;

        if (data != null) {
            thisClosure = (Closure) currentClosure.getChildClosureForMethod(data.toString());
        }
        Object type = "";
        Object name = "";
        Object returnValue = "";
        // if == 2 then its a decl in the for loop otherwise a statment

        if(node.jjtGetNumChildren()==3){
            type = this.visit((SimpleNode) node.jjtGetChild(0), data);
            name = this.visit((SimpleNode) node.jjtGetChild(1), data);
            if(thisClosure.variableExists(name.toString())) {
                return "Variable is already defined";
            }
            thisClosure.addBoundVariable(name.toString(), type.toString());
            thisClosure.addBoundVariableValue(name.toString(), true);

            if (name.toString().isEmpty() || type.toString().isEmpty()){
                return "Variable doesnt exist";
            }

            returnValue = this.visit((SimpleNode) node.jjtGetChild(node.jjtGetNumChildren()-1), type);
        } else if (node.jjtGetNumChildren()==1) {
            name = this.visit((SimpleNode) node.jjtGetChild(0), data);

            if(thisClosure.variableExistsAnywhere(name.toString())) {
                type = thisClosure.getVariableTypeAnywhere(name.toString()).getValue();
                thisClosure.addBoundVariable(name.toString(), type.toString());
                thisClosure.addBoundVariableValue(name.toString(), true);

            }


            if (name.toString().isEmpty() || type.toString().isEmpty()){
                return "Variable doesnt exist";
            }

            returnValue = this.visit((SimpleNode) node.jjtGetChild(node.jjtGetNumChildren()-1).jjtGetChild(0), type);
        }



        if (returnValue.toString().isEmpty()){
            return "";
        } else {
            return returnValue.toString();
        }
    }

    @Override
    public Object visit(ASTFORCOND node, Object data) {
        String type = "";
        Set<String> validValues = new HashSet<>(Arrays.asList("int", "boolean"));
        Closure thisClosure = this.currentClosure;
        if (data != null) {
            thisClosure = (Closure) currentClosure.getChildClosureForMethod(data.toString());
        }
        boolean isBool = false;
        if (isBoolStatement(node, data)){
            return "";
        } else {
            return "The for loop requires a condition";
        }

    }

    @Override
    public Object visit(ASTFOROPER node, Object data) {
        Closure thisClosure = this.currentClosure;
        Closure saveClosure = this.currentClosure;
        if (data!=null){
            thisClosure = (Closure) currentClosure.getChildClosureForMethod(data.toString());

        }
        this.currentClosure=thisClosure;
        Object returnValue = this.visit((SimpleNode) node.jjtGetChild(0),"int");

        if(thisClosure.variableExists(returnValue.toString())) {
            returnValue = thisClosure.getVariableTypeMap().get(returnValue.toString());

            returnValue = this.visit((SimpleNode) node.jjtGetChild(0).jjtGetChild(0),returnValue);
            this.currentClosure = saveClosure;
            if (returnValue.toString().isEmpty()){
                return "";
            }else {
                return returnValue.toString();
            }

        } else if (returnValue.toString().isEmpty()){
            if (((SimpleNode) node.jjtGetChild(0)).toString().equals("OPERATION_PRIO_13") || ((SimpleNode) node.jjtGetChild(0)).toString().equals("OPERATION_PRIO_14")){
                // get child of prio13/14 because the child needs to be tha variable
                returnValue = this.visit((SimpleNode) node.jjtGetChild(0).jjtGetChild(0),"int");
                if(thisClosure.variableExists(returnValue.toString())) {
                    return "";

                } else {
                    return "Fro-loop OROPER variable doesnt exist";
                }
            } else {
                return "Unknown Fro-loop OROPER";
            }
        } else {
            return "Unknown Token in OROPER";
        }
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

            if(currentClosure.variableExists(returnValue.toString())){
                //  Object value = this.visit((SimpleNode) node.jjtGetChild(0).jjtGetChild(0), data);
                returnValue = currentClosure.getVariableTypeMap().get(returnValue.toString());
                /*Object value = this.visit((SimpleNode) node.jjtGetChild(1), data);
                if (value.toString().equals("")) {

                    return returnValue.toString();
                }*/
            }
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

            if (currentClosure.variableExists(returnValue.toString())) {
                returnValue = (currentClosure.getVariableTypeMap().get(returnValue.toString()));
            }

            if (returnValue.toString().equals("int")){
                if (isBoolStatement(node, data)){
                    returnValue = "boolean";
                }
            }
            if (data.toString().equals(returnValue.toString()) || returnValue.equals("")) {
                return "";
            }  else {
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
            if (currentClosure.variableExists(returnValue.toString())) {
                returnValue = (currentClosure.getVariableTypeMap().get(returnValue.toString()));
            }
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

            if (currentClosure.variableExists(returnValue.toString())) {
                returnValue = (currentClosure.getVariableTypeMap().get(returnValue.toString()));
            }
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
            if (currentClosure.variableExists(returnValue.toString())) {
                returnValue = (currentClosure.getVariableTypeMap().get(returnValue.toString()));
            }
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
        Set<String> validValues = new HashSet<>(Arrays.asList("int", "boolean"));

        if (validValues.contains(data.toString())) {
            Object returnValue = this.visit((SimpleNode) node.jjtGetChild(0), data);
            if (currentClosure.variableExists(returnValue.toString())) {
                returnValue = (currentClosure.getVariableTypeMap().get(returnValue.toString()));
            }
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
            if (currentClosure.variableExists(returnValue.toString())) {
                returnValue = (currentClosure.getVariableTypeMap().get(returnValue.toString()));
            }
            if (data.toString().equals(returnValue.toString()) || returnValue.equals("")) {
                return "";
            }  else {
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

    private boolean isBoolStatement(SimpleNode node, Object data){
        boolean isBool = false;
        if (node.toString().equals("OPERATION_PRIO_9")){
            return true;
        }
        Closure thisClosure = this.currentClosure;
        if (data!=null){
            thisClosure = (Closure) currentClosure.getChildClosureForMethod(data.toString());
        }
        String returnValue = "";
        for (int children = 0; children<node.jjtGetNumChildren(); children++){
            if(!node.jjtGetChild(children).toString().equals("OPERATION_PRIO_4_AND_3")){
                returnValue = this.visit((SimpleNode) node.jjtGetChild(children), "int").toString();
                if(thisClosure.variableExists(returnValue.toString())) {
                    returnValue = thisClosure.getVariableTypeAnywhere(returnValue.toString()).getValue();

                }else if (!returnValue.equals("int") && !returnValue.equals("")){
                    return false;
                }
            } else {
                String result = this.visit((SimpleNode) node.jjtGetChild(children), "boolean").toString();
                if (!result.equals("")){
                    return  false;
                }
            }

        }
        for (int children = 0; children<node.jjtGetNumChildren(); children++){
            isBool = isBoolStatement((SimpleNode) node.jjtGetChild(children), data);
            if (isBool){
                return isBool;
            }

        }
        return isBool;
    }

}
