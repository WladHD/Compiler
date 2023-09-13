package de.fh.semantic;

import de.fh.javacc.generated.SimpleNode;
import de.fh.semantic.closure.Closure;
import de.fh.semantic.err.SemanticException;
import de.fh.semantic.err.UnknownSemanticException;

import javax.swing.plaf.ColorUIResource;
import javax.xml.transform.stream.StreamSource;
import java.lang.reflect.Type;
import java.sql.Statement;
import java.util.*;

public class SemanticAnalyzer implements ISemanticAnalyzer<SimpleNode> {

    @Override
    public void doSemanticCheck(SimpleNode rootNode) throws SemanticException {
        Closure mainClosure = new Closure(null);
        doTreeSemanticCheck(rootNode, mainClosure);


    }

    public String doTreeSemanticCheck(SimpleNode rootNode, Closure currentClosure) throws SemanticException{
        String iDontFuckingKnowHowToNameIt = "";
        boolean semanticOK = true;
        String errorMessage = null;
        String rootNodeAsString = rootNode.toString();


        if(rootNodeAsString != null){
            label:


                switch (rootNodeAsString) {
                    case "PROGRAM":
                        for(int i = 0; i < rootNode.jjtGetNumChildren(); i++) {
                            doTreeSemanticCheck((SimpleNode) rootNode.jjtGetChild(i), currentClosure);
                        }
                        break;
                    case "DECL":
                        String type = "";
                        Object value = null;
                        String name = "";
                        boolean isMethod = false;
                        for (int children = 0; children < rootNode.jjtGetNumChildren(); children++) {
                            String returnValue = doTreeSemanticCheck((SimpleNode) rootNode.jjtGetChild(children), currentClosure);
                            switch (((SimpleNode) rootNode.jjtGetChild(children)).toString()) {
                                case "TYPE" -> {
                                    type = returnValue;
                                }
                                case "VAR_METHOD_NAME" -> {
                                    name = returnValue;
                                }
                                case "CONCLUDED_VAR_DEC"-> {
                                    value = collectChildren((SimpleNode) rootNode.jjtGetChild(children).jjtGetChild(0));
                                }
                                case "MethDecl"-> {
                                    isMethod = true;
                                }
                            }
                        }


                        if (!name.isEmpty() && !type.isEmpty() && !isMethod) {
                            currentClosure.addBoundVariable(name, type);
                            if(!(value == null)){
                                currentClosure.addBoundVariableValue(name, value);
                            }
                        } else if (!name.isEmpty() && !type.isEmpty() && isMethod) {

                            currentClosure.addBoundMethod(name, type, currentClosure.createNewClosure());

                        } else {
                            if (!name.isEmpty() && type.isEmpty()){
                                errorMessage = "You tried to Declare a Variable without a Type";
                                semanticOK = false;
                                break;
                            }
                            System.out.println("Decl else case with type is Empty");
                        }

                        break;
                    case "STATEMENT":
                        if (rootNode.jjtGetNumChildren()>1){
                            errorMessage = "Statment with more than one Child";
                            semanticOK = false;
                            break label;
                        }
                        // get name because Statement is always followed by VAR_METHOD_NAME
                        String varNameStatement = doTreeSemanticCheck((SimpleNode) rootNode.jjtGetChild(0), currentClosure);
                        // get value that is assigned to the var because var is always followed by assignment
                        String varValueStatement = doTreeSemanticCheck((SimpleNode) rootNode.jjtGetChild(0).jjtGetChild(0), currentClosure);


                        boolean itWorked = currentClosure.updateVariableValue(varNameStatement, varValueStatement);

                        if (!itWorked){
                            System.out.println(varNameStatement);
                            System.out.println(varValueStatement);
                            errorMessage = "You tried to Update a Variable that does not exist";
                            semanticOK = false;
                            break;
                        }

                        break;
                    case "BLOCK":
                        doTreeSemanticCheck((SimpleNode) rootNode.jjtGetChild(0), (Closure) currentClosure.createNewClosure());

                        break;
                    case "VARIABLE_ASSIGNMENT_PRIO_1":
                        String assingedDType = assignmentDatatypeCheck(rootNode, currentClosure);

                        if (!assingedDType.equals("Int") && !assingedDType.equals("String") && !assingedDType.equals("Bool")) {
                            errorMessage = assingedDType;
                            semanticOK = false;
                            break label;

                        } else {

                            boolean opSemanticCheck = operatorTypeCheck(rootNode, assingedDType);

                            if (opSemanticCheck == false) {
                                errorMessage = "Wrong Operator for D-Type '" + assingedDType + "'";
                                semanticOK = false;
                                break label;
                            }

                            return assingedDType;
                        }

                    case "TYPE":
                        return rootNode.jjtGetValue().toString();

                    case "VAR_METHOD_NAME":
                        return rootNode.jjtGetValue().toString();

                    case "METHDECL":
                        return "MethDecl";

                    case "CONCLUDED_VAR_DEC":
                        if (rootNode.jjtGetNumChildren() == 1) {
                            return doTreeSemanticCheck((SimpleNode) rootNode.jjtGetChild(0), currentClosure);
                        } else {

                            if(!(rootNode.jjtGetNumChildren() == 0)){
                                System.out.println("More Children of Concluded_Var_Dec then expected");
                                System.out.println(rootNode.jjtGetNumChildren());
                            }

                        }
                        break;

                    case "REQUESTER_VAR_METHOD":
                        if (rootNode.jjtGetNumChildren() == 1) {
                            return doTreeSemanticCheck((SimpleNode) rootNode.jjtGetChild(0), currentClosure);
                        } else {

                            if(!(rootNode.jjtGetNumChildren() == 0)){
                                System.out.println("More Children of REQUESTER_VAR_METHOD then expected");
                                System.out.println(rootNode.jjtGetNumChildren());
                            }

                        }
                    default:
                        errorMessage = "Unkown Token '" + rootNodeAsString + "'";
                        semanticOK = false;
                        break label;
                }

        }

        //System.out.println(rootNode.jjtGetChild(0));
        //System.out.println(rootNode.jjtGetNumChildren());
        if(!semanticOK){
            throw new UnknownSemanticException(errorMessage);
        }
        return  iDontFuckingKnowHowToNameIt;
    }

    private String assignmentDatatypeCheck(SimpleNode currentNode, Closure currentClosure){
        String returnValue = "";
        for(int i = 0; i < currentNode.jjtGetNumChildren(); i++){
            SimpleNode currentChild = (SimpleNode) currentNode.jjtGetChild(i);
            if(currentChild.toString().equals("ATOM_INT")){

                if(returnValue.isEmpty()){
                    returnValue = "Int";
                } else if (!returnValue.equals("Int")) {
                    returnValue = "Datatype Error can't assign Int to " + returnValue;
                    break;
                }
                
            } else if (currentChild.toString().equals("ATOM_STRING")) {

                if(returnValue.isEmpty()){
                    returnValue = "String";
                } else if (!returnValue.equals("String")) {
                    returnValue = "Datatype Error can't assign String to " + returnValue;
                    break;
                }

            } else if (currentChild.toString().equals("ATOM_CHAR")) {

                if(returnValue.isEmpty()){
                    returnValue = "Char";
                } else if (!returnValue.equals("Char")) {
                    returnValue = "Datatype Error can't assign Char to " + returnValue;
                    break;
                }

            }else if (currentChild.toString().equals("ATOM_BOL")) {

                if(returnValue.isEmpty()){
                    returnValue = "Bool";
                } else if (!returnValue.equals("Bool")) {
                    returnValue = "Datatype Error can't assign Bool to " + returnValue;
                    break;
                }

            } else if (currentChild.toString().equals("GET_VAR_METHOD")) {
                if(returnValue.isEmpty()){

                    if (checkVariableTypeMap(currentChild,currentClosure,true) == null){

                        if (checkVariableTypeMap(currentChild,currentClosure,false) == null){
                            returnValue = "Can't assign a variable that hasn't been initialized yet";
                            break;
                        } else {
                            returnValue = checkVariableTypeMap(currentChild,currentClosure,false);
                        }
                    } else {
                        returnValue = checkVariableTypeMap(currentChild,currentClosure,true);
                    }

                } else {
                    String varTypRetrun = "";
                    if (checkVariableTypeMap(currentChild,currentClosure,true) == null){

                        if (checkVariableTypeMap(currentChild,currentClosure,false) == null){
                            returnValue = "Can't assign a variable that hasn't been initialized yet";
                            break;
                        } else {
                            varTypRetrun = checkVariableTypeMap(currentChild,currentClosure,false);
                        }
                    } else {
                        varTypRetrun = checkVariableTypeMap(currentChild,currentClosure,true);
                    }

                    if (!returnValue.equals(varTypRetrun)) {
                        returnValue = "Datatype Error can't assign Bool to " + returnValue;
                        break;
                    }
                }

            } else if (currentChild.toString().contains("OPERATION_PRIO")) {

                if(returnValue.isEmpty()){
                    returnValue = assignmentDatatypeCheck(currentChild, currentClosure);
                } else if (!returnValue.equals(assignmentDatatypeCheck(currentChild, currentClosure))) {
                    returnValue = "Datatype Error can't assign "+ assignmentDatatypeCheck(currentChild, currentClosure) + " to " + returnValue;
                    break;
                }
                returnValue = assignmentDatatypeCheck(currentChild, currentClosure);
            } else {
                returnValue = "Unknown Assignment Value '" + currentChild.toString() + "'";
                break;
            }
        }

        return returnValue;
    }

    private String checkVariableTypeMap (SimpleNode currentChild, Closure currentClosure, boolean CheckForBoundOnly){
        String returnValue = "";
        AbstractMap.SimpleEntry<String, Object> varTypMap = currentClosure.getVariableTypeAndValue(currentChild.jjtGetValue().toString(), CheckForBoundOnly);

        if (varTypMap != null) {
            String variableType = varTypMap.getKey();
            Object variableValue = varTypMap.getValue();

            if (variableType != null && variableValue != null) {
                returnValue = variableType;

            } else {
                returnValue = null;
            }

        } else {
            returnValue = null;
        }
        return returnValue;
    }
    
    private boolean operatorTypeCheck (SimpleNode currentNode, String dataType){
        boolean opIsOkay = true;
        for(int i = 0; i < currentNode.jjtGetNumChildren(); i++){
            SimpleNode currentChild = (SimpleNode) currentNode.jjtGetChild(i);


            if ((currentChild.toString().contains("OPERATION_PRIO"))){

                if(!operatorTypeCheck(currentChild, dataType)){
                    return false;
                }

                switch (dataType) {
                    case "Int" -> {

                        Set<String> validValues = new HashSet<>(Arrays.asList("+", "-", "*", "/", "%", "++", "--"));
                        if (validValues.contains(currentChild.jjtGetValue().toString())) {
                            return opIsOkay;
                        } else {
                            System.out.println("Wrong Operator for Int'" + currentChild.jjtGetValue().toString() + "'");
                            return false;
                        }

                    }
                    case "String" -> {
                        if (currentChild.jjtGetValue().toString().equals("+")) {
                            return opIsOkay;
                        } else {
                            System.out.println("Wrong Operator for String'" + currentChild.jjtGetValue().toString() + "'");
                            return false;
                        }
                    }
                    case "Bool" -> {

                        Set<String> validValues = new HashSet<>(Arrays.asList(">", "<", "==", ">=", "<=", "!=", "||", "&&", "!"));
                        if (validValues.contains(currentChild.jjtGetValue().toString())) {
                            return opIsOkay;
                        } else {
                            System.out.println("Wrong Operator for Boolean'" + currentChild.jjtGetValue().toString() + "'");
                            return false;
                        }

                    }
                    case "Char" -> {

                        Set<String> validValues = new HashSet<>(Arrays.asList("+", "-"));
                        if (validValues.contains(currentChild.jjtGetValue().toString())) {
                            return opIsOkay;
                        } else {
                            System.out.println("Wrong Operator for Char'" + currentChild.jjtGetValue().toString() + "'");
                            return false;
                        }

                    }
                }


            }

        }
        return opIsOkay;
    }

    public Object collectChildren(SimpleNode node) {
        // Check if the node is null (base case for recursion)
        if (node == null) {
            return null;
        }

        // Initialize a list to store the children values
        List<Object> childrenList = new ArrayList<>();

        // Iterate through the children of the current node
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            SimpleNode childNode = (SimpleNode) node.jjtGetChild(i);
            // Get the value of the current child node
            Object childValue = childNode.jjtGetValue();
            // Recursively collect children of the current child node
            Object childResult = collectChildren(childNode);
            // Add the collected child value to the list
            childrenList.add(childValue);
            // Add the result of child node traversal (if needed)
            childrenList.add(childResult);
        }

        // Return the list of collected children values
        return childrenList;
    }

}
