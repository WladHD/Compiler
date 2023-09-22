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
        return node.jjtGetValue();
    }

    @Override
    public Object visit(ASTGET_METHOD_PARAMS node, Object data) {
        if (node.jjtGetNumChildren()>0){
            Object returnValue = this.visit((SimpleNode) node.jjtGetChild(0),data);
            if(returnValue.toString().isEmpty()){
                return "";
            }else {
                return returnValue.toString();
            }
        }else {
            return "";
        }
    }

    @Override
    public Object visit(ASTPARAM node, Object data) {
        if (node.jjtGetNumChildren()>0){
            boolean paramsOk = checkParams(node,data);
            if (paramsOk){
                return "";
            }else {
                return "The inserted Parameters are wrong";
            }
        } else {
            return "PARAM didn't have a child";
        }

    }

    @Override
    public Object visit(ASTSTATEMENT node, Object data) {
        Object name = this.visit((SimpleNode) node.jjtGetChild(0), data);

        String type = "";
        if (currentClosure.getVariableThisClosure(name.toString()) || currentClosure.getMethodThisClosure(name.toString())) {


            if (node.jjtGetChild(0).jjtGetNumChildren() > 0) {

                if (currentClosure.getVariableThisClosure(name.toString())) {
                    type = currentClosure.getVariableTypeMap().get(name.toString());

                } else if (currentClosure.getMethodThisClosure(name.toString())) {
                    type = currentClosure.getMethodReturnTypeMap().get(name.toString());

                }

            } else {
                return "Statement child had no Children";
            }

        }
        if (data != null && type.isEmpty()) {

            if (currentClosure.parameterExists(data.toString(), name.toString())) {
                if (node.jjtGetChild(0).jjtGetNumChildren() > 0) {
                    type = currentClosure.getParameterType(data.toString(), name.toString());
                } else {
                    return "Statement child had no Children";
                }
            }
        }
        if (currentClosure.getParent() != null && type.isEmpty()) {
            Set<String> validValues = new HashSet<>(Arrays.asList("Path", "Files", "System", "Map", "Set"));
            if (currentClosure.getParent().getVariableThisClosure(name.toString())){
                if (node.jjtGetChild(0).jjtGetNumChildren() > 0) {
                    type = currentClosure.getParent().getVariableTypeMap().get(name.toString());
                } else {
                    return "Statement child had no Children";
                }

            }  else if (validValues.contains(name.toString())) {
                // System is node.jjtGetChild(0) and i need its child
                Object getSystemOperator = this.visit((SimpleNode) node.jjtGetChild(0).jjtGetChild(0), data);

                if(getSystemOperator.toString().equals(".")){
                    // calling Function that is to be executed after "System."

                     // define all System. Operations
                    boolean isOk = checkCalls((SimpleNode) node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0), name.toString());
                    if(isOk){
                        name = "";
                    } else {
                        return "The call of " + name + " didn't work. Wrong Input";
                    }
                }


            } else {
                return this.visit((SimpleNode) node.jjtGetChild(0), data);
            }

        }

        if (!type.isEmpty()){

            Object returnValue = this.visit((SimpleNode) node.jjtGetChild(0).jjtGetChild(0), type);
            if (returnValue.toString().equals("")) {

                boolean itWorked = currentClosure.updateVariableValue(name.toString(), true);
                if (data != null){
                    if (currentClosure.parameterExists(data.toString(), name.toString()) && !itWorked) {

                        currentClosure.addBoundVariableValueWithoutCheck(name.toString(), true);
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

        Object type = this.visit((SimpleNode) node.jjtGetChild(0), null);

        Object name = this.visit((SimpleNode) node.jjtGetChild(1), null);

        SimpleNode concludeMeth = (SimpleNode) node.jjtGetChild(2);

        if (concludeMeth.toString().equals("METHDECL")){

            itWorked = this.currentClosure.addBoundMethod(name.toString(), type.toString(), this.currentClosure.createNewClosure());
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

            itWorked = this.currentClosure.addBoundVariable(name.toString(), type.toString());
            // open tree for var declare if value exists

            if(!itWorked){
                return "The Variable already exists";
            }
            if (concludeMeth.jjtGetNumChildren() == 1){

                if(type.toString().equals("Map")){

                    String type1 = this.visit((SimpleNode) node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0), null).toString();
                    String type2 = this.visit((SimpleNode) node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(1), null).toString();
                    itWorked = checkMap((SimpleNode) concludeMeth.jjtGetChild(0).jjtGetChild(0) ,type1,type2);
                    if (!itWorked){
                        return "Map could not be declared";
                    }
                } else if (type.toString().equals("Set")) {
                    type = this.visit((SimpleNode) node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0), null).toString();

                    Object value = this.visit((SimpleNode) concludeMeth.jjtGetChild(0), type);
                    if (value.toString().equals("")) {


                        itWorked = this.currentClosure.setBoundVariableValue(name.toString());

                        if (!itWorked) {

                            return "The variable " + name.toString() + " already has a value";

                        }


                    } else {
                        return value.toString();
                    }
                } else {
                    // access VARIABLE_ASSIGNMENT_PRIO_1
                    Object value = this.visit((SimpleNode) concludeMeth.jjtGetChild(0), type);
                    if (value.toString().equals("")) {


                        itWorked = this.currentClosure.setBoundVariableValue(name.toString());

                        if (!itWorked) {

                            return "The variable " + name.toString() + " already has a value";

                        }


                    } else {
                        return value.toString();
                    }
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
        Object returnValue = this.visit((SimpleNode) node.jjtGetChild(0),data);
        if(returnValue.toString().isEmpty()){
            return "";
        } else {
            return returnValue.toString();
        }

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
        if (currentClosure.getMethodThisClosure(data.toString())){
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
    public Object visit(ASTTYPE_ARRAY node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTVAR_METHOD_NAME node, Object data) {
        return node.jjtGetValue();
    }

    @Override
    public Object visit(ASTBLOCK node, Object data) {
        // overwrite current closure and save the old current closure as long as the block is edited
        Closure thisClosure = this.currentClosure;
        Closure saveClosure = this.currentClosure;
        if (data != null) {
            thisClosure = (Closure) currentClosure.getChildClosureForMethod(data.toString());

        }
        this.currentClosure = thisClosure;

        Object returnValue = null;
        for(int children = 0; children< node.jjtGetNumChildren(); children++){
            returnValue = this.visit((SimpleNode) node.jjtGetChild(children), data.toString());
            if (!returnValue.toString().equals("")){
                this.currentClosure = saveClosure;
                return returnValue.toString();
            }
        }
        this.currentClosure = saveClosure;
        return "";
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
            } else if (thisClosure.getVariableThisClosure(returnValue.toString())) {
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
    public Object visit(ASTFOR node, Object data) {
        String name = "for";
        boolean itWorked = false;
        int iteration = 0;

        while(!itWorked){
            iteration++;
            itWorked = currentClosure.addBoundMethod(name + iteration, "void", currentClosure.createNewClosure());
        }
        Closure thisClosure = (Closure) currentClosure.getChildClosureForMethod(name+iteration);
        Closure saveClosure = this.currentClosure;

        this.currentClosure = thisClosure;

        for (int foriteration = 0; foriteration<node.jjtGetNumChildren()-1; foriteration++){
            Object returnValue = this.visit((SimpleNode) node.jjtGetChild(foriteration),name+iteration);

            if (!returnValue.toString().isEmpty()){
                return returnValue.toString();
            }
        }
        this.currentClosure = saveClosure;


        return "";
    }

    @Override
    public Object visit(ASTFOR_BODY node, Object data) {
        return this.visit((SimpleNode) node.jjtGetChild(0), data);
    }

    @Override
    public Object visit(ASTFOR_BODY_NORMAL node, Object data) {
        for (int children = 0; children<node.jjtGetNumChildren();children++){
            Object result = this.visit((SimpleNode) node.jjtGetChild(children), data);
            if(!result.toString().isEmpty()){
                return result.toString();
            }
        }
        return "";
    }

    @Override
    public Object visit(ASTFOR_BODY_EACH node, Object data) {
        Object returnValue = this.visit((SimpleNode) node.jjtGetChild(0),data);
        if(!returnValue.toString().isEmpty()){
            return returnValue.toString();
        }
        return "";
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
        Object type = "";
        Object name = "";
        Object returnValue = "";
        boolean itWorked;
        // if == 2 then it's a decl in the for loop otherwise a statement
        if(node.jjtGetNumChildren()==3){
            type = this.visit((SimpleNode) node.jjtGetChild(0), data);
            name = this.visit((SimpleNode) node.jjtGetChild(1), data);

            if (name.toString().isEmpty() || type.toString().isEmpty()){
                return "FORINIT Variable doesnt exist";
            }

            if(currentClosure.getVariableThisClosure(name.toString())) {
                return "FORINIT Variable is already defined: " + name;
            }

            itWorked = currentClosure.addBoundVariable(name.toString(), type.toString());
            if (!itWorked){
                return "FORINIT Variable is already declared";
            }
            itWorked =currentClosure.setBoundVariableValue(name.toString());

            if (!itWorked){
                return "FORINIT Variable value couldn't be set";
            }

            returnValue = this.visit((SimpleNode) node.jjtGetChild(node.jjtGetNumChildren()-1), type);

        } else if (node.jjtGetNumChildren()==2) {

            type = this.visit((SimpleNode) node.jjtGetChild(0), data);
            name = this.visit((SimpleNode) node.jjtGetChild(1), data);

            if (name.toString().isEmpty() || type.toString().isEmpty()){
                return "FORINIT Variable doesnt exist";
            }

            if(currentClosure.getVariableThisClosure(name.toString())) {
                return "FORINIT Variable is already defined: " + name;
            }

            itWorked = currentClosure.addBoundVariable(name.toString(), type.toString());
            if (!itWorked){
                return "FORINIT Variable is already declared";

            }
        } else if (node.jjtGetNumChildren()==1) {
            name = this.visit((SimpleNode) node.jjtGetChild(0), data);

            if(currentClosure.getVariableAnywhere(name.toString())) {
                type = currentClosure.getVariableTypeAnywhere(name.toString());
                itWorked = currentClosure.setVariableValueAnywhere(name.toString());
                if(!itWorked){
                    return "FORINIT was unable to set the variable value anywhere";
                }
            }


            if (name.toString().isEmpty() || type.toString().isEmpty()){
                return "FORINIT Variable doesnt exist";
            }

            returnValue = this.visit((SimpleNode) node.jjtGetChild(node.jjtGetNumChildren()-1).jjtGetChild(0), type);
        } else {
            return "FORINIT Unknown number of children: " + node.jjtGetNumChildren();
        }



        if (returnValue.toString().isEmpty()){
            return "";
        } else {
            return returnValue.toString();
        }
    }

    @Override
    public Object visit(ASTFORCOND node, Object data) {
        int y;
        for (y = 0; y < 21; y++){

        }
        if (isBoolStatement(node, data)){
            return "";
        } else {
            return "FORCOND Requires a Condition";
        }

    }

    @Override
    public Object visit(ASTFOROPER node, Object data) {

        Object returnValue = this.visit((SimpleNode) node.jjtGetChild(0),"int");
        if(currentClosure.getVariableThisClosure(returnValue.toString())) {

            returnValue = currentClosure.getVariableTypeMap().get(returnValue.toString());

            returnValue = this.visit((SimpleNode) node.jjtGetChild(0).jjtGetChild(0),returnValue);

            boolean itWorks = varGetsIncrement(node);
            if (returnValue.toString().isEmpty() && itWorks){
                return "";
            } else if (returnValue.toString().isEmpty() && !itWorks) {
                return "FOROPER The Variable in the for loop never changes";
            } else {
                return returnValue.toString();
            }

        } else if (returnValue.toString().isEmpty()){
            if (((SimpleNode) node.jjtGetChild(0)).toString().equals("OPERATION_PRIO_13") || ((SimpleNode) node.jjtGetChild(0)).toString().equals("OPERATION_PRIO_14")){
                // get child of prio13/14 because the child needs to be the variable
                returnValue = this.visit((SimpleNode) node.jjtGetChild(0).jjtGetChild(0),"int");
                if(currentClosure.getVariableAnywhere(returnValue.toString())) {
                    return "";

                } else {
                    return "FOROPER variable doesnt exist: " + returnValue.toString();
                }
            } else {
                return "FOROPER Unknown";
            }
        } else {
            if (returnValue.toString().isEmpty()){
                return "FOROPER Unknown Token ";
            } else {
                return returnValue.toString();
            }

        }
    }

    @Override
    public Object visit(ASTATOM_ARRAY node, Object data) {
        for (int children = 0; children<node.jjtGetNumChildren();children++){
            Object returnValue  = this.visit((SimpleNode) node.jjtGetChild(children),data);
            if (!returnValue.toString().equals(data.toString())){
                return "ATOM_ARRAY wrong data type inside of array";
            }
        }
        return "";
    }

    @Override
    public Object visit(ASTATOM_SET node, Object data) {
        for (int children = 0; children<node.jjtGetNumChildren(); children++){
            Object returnValue = this.visit((SimpleNode) node.jjtGetChild(children),data);
            if (!data.toString().equals(returnValue.toString())){
                return "ATOM_SET you tried to assign " + returnValue.toString() + " to "+ data.toString();
            }

        }
        return "";
    }

    @Override
    public Object visit(ASTATOM_MAP node, Object data) {
        return "Map";
    }

    @Override
    public Object visit(ASTATOM_PATHELEMENT node, Object data) {
        if (node.jjtGetValue().toString().isEmpty()){
            return "ATOM_PATHELEMENT is Empty";
        }
        if (node.jjtGetNumChildren()>0){
             return this.visit((SimpleNode) node.jjtGetChild(0),data);
        }

        return "";

    }

    @Override
    public Object visit(ASTMAP_PAIR node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMAP_KEY node, Object data) {
        return this.visit((SimpleNode) node.jjtGetChild(0), data);
    }

    @Override
    public Object visit(ASTMAP_VALUE node, Object data) {
        return this.visit((SimpleNode) node.jjtGetChild(0), data);
    }

    @Override
    public Object visit(ASTARRAY_ELEMENT node, Object data) {
        return this.visit((SimpleNode) node.jjtGetChild(0), data);
    }

    @Override
    public Object visit(ASTSET_ELEMENT node, Object data) {
        return this.visit((SimpleNode) node.jjtGetChild(0),data);
    }

    @Override
    public Object visit(ASTVARIABLE_ASSIGNMENT_PRIO_1 node, Object data) {
        Object returnValue = null;
        Set<String> validValues = new HashSet<>(Arrays.asList("int", "String", "char", "boolean"));

        for(int children = 0; children < node.jjtGetNumChildren(); children++){
            returnValue = this.visit((SimpleNode) node.jjtGetChild(children), data);

            if(currentClosure.getVariableThisClosure(returnValue.toString())){
                //  Object value = this.visit((SimpleNode) node.jjtGetChild(0).jjtGetChild(0), data);
                returnValue = currentClosure.getVariableTypeMap().get(returnValue.toString());

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

            if (currentClosure.getVariableThisClosure(returnValue.toString())) {
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
            if (currentClosure.getVariableThisClosure(returnValue.toString())) {
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

            if (currentClosure.getVariableThisClosure(returnValue.toString())) {
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
            if (currentClosure.getVariableThisClosure(returnValue.toString())) {
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
            if (currentClosure.getVariableThisClosure(returnValue.toString())) {
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
            if (currentClosure.getVariableAnywhere(returnValue.toString())) {
                returnValue = (currentClosure.getVariableTypeAnywhere(returnValue.toString()));
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
        String returnValue = "";
        for (int children = 0; children<node.jjtGetNumChildren(); children++){
            if(!node.jjtGetChild(children).toString().equals("OPERATION_PRIO_4_AND_3")){
                returnValue = this.visit((SimpleNode) node.jjtGetChild(children), "int").toString();
                if(!returnValue.isEmpty()) {
                    if (currentClosure.getVariableAnywhere(returnValue.toString())) {
                        returnValue = currentClosure.getVariableTypeAnywhere(returnValue.toString());
                        if (!returnValue.toString().equals("int") && !returnValue.toString().equals("boolean")) {
                            return false;
                        }
                    } else if (!returnValue.equals("int")) {
                        return false;
                    }
                }
            } else {
                String result = this.visit((SimpleNode) node.jjtGetChild(children), "boolean").toString();
                if (!result.isEmpty()){
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

    private boolean varGetsIncrement(SimpleNode node){
        boolean itsTrue = false;

        if (node.toString().equals("OPERATION_PRIO_11")||node.toString().equals("OPERATION_PRIO_12")|| node.toString().equals("OPERATION_PRIO_13")|| node.toString().equals("OPERATION_PRIO_14")){
            return true;
        } else {
            if (node.toString().equals("VARIABLE_ASSIGNMENT_PRIO_1")&& !node.jjtGetValue().toString().equals("=")){
                return true;
            }
        }
        for(int children = 0; children<node.jjtGetNumChildren();children++){
            itsTrue = varGetsIncrement((SimpleNode) node.jjtGetChild(children));

        }

        return itsTrue;
    }

    // Closure must be correctly set before calling
    // Param check for System needs to be done
    private boolean checkParams(SimpleNode node , Object data){
        boolean paramsOk = true;
        // check how many children Params has
        if(node.jjtGetNumChildren() == 1){
            // if it has only one child and no grandchildren
            if(node.jjtGetChild(0).jjtGetNumChildren()==0){
                // if no grandchildren than check if it is operator if not than true
                return !node.jjtGetChild(0).toString().contains("OPERATION");
            } else {

            }
        } else if (node.jjtGetNumChildren() > 1) {
            
        }
        for (int children = 0; children< node.jjtGetNumChildren(); children++){
            Object returnValue = this.visit((SimpleNode) node.jjtGetChild(children),data);
        }
        char a = 'a';
        System.out.println(a>5);
        return paramsOk;
    }
    // checks if a called object like "String." gets called with something valid
    // node is REQUESTER_VAR_METHOD after the "."
    // data contains the data type of the Object getting called (Path, Files, System, Map, Set)
    private boolean checkCalls(SimpleNode node, Object data){

        Object Value = this.visit(node,data);
        if (data.toString().equals("Path")){

            if (Value.toString().equals("ask")){
                return true;
            } else if (Value.toString().equals("update")) {
                return true;
            } else if (Value.toString().equals("remove")) {
                return true;
            } else if (Value.toString().equals("copyTo")) {
                return true;
            } else if (Value.toString().equals("moveTo")) {
                return true;
            } else if (Value.toString().equals("toString")) {
                return true;
            } else if (Value.toString().equals("files")) {
                return true;
            } else {
                return false;
            }

        } else if (data.toString().equals("Files")) {

            if (Value.toString().equals("remove")){
                return true;
            } else if (Value.toString().equals("rename")) {
                return true;
            } else if (Value.toString().equals("moveTo")) {
                return true;
            } else if (Value.toString().equals("getContent")) {
                return true;
            } else if (Value.toString().equals("toString")) {
                return true;
            } else {
                return false;
            }

        } else if (data.toString().equals("System")) {

            if (Value.toString().equals("OutputStream")){
                return true;
            } else if (Value.toString().equals("InputStream")) {
                return true;
            } else if (Value.toString().equals("print")) {
                return true;
            } else if (Value.toString().equals("println")) {
                return true;
            } else if (Value.toString().equals("readString")) {
                return true;
            } else {
                return false;
            }

        } else if (data.toString().equals("Map")) {

            if (Value.toString().equals("containsKey")){
                return true;
            } else if (Value.toString().equals("get")) {
                return true;
            } else if (Value.toString().equals("toString")) {
                return true;
            } else {
                return false;
            }

        } else if (data.toString().equals("Set")) {

            if (Value.toString().equals("addAllContents")){
                return true;
            } else if (Value.toString().equals("contains")) {
                return true;
            } else if (Value.toString().equals("diff")) {
                return true;
            } else if (Value.toString().equals("inter")) {
                return true;
            } else if (Value.toString().equals("toString")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    private boolean checkMap(SimpleNode node, String type1, String type2){
        for (int children = 0; children < node.jjtGetNumChildren(); children++){
            if (!(this.visit((SimpleNode) node.jjtGetChild(0).jjtGetChild(0), type1)).toString().equals(type1) && !(this.visit((SimpleNode) node.jjtGetChild(0).jjtGetChild(1), type2)).toString().equals(type2)){
                return false;
            }

        }
        return true;
    }
}
