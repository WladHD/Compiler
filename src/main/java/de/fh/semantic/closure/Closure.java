package de.fh.semantic.closure;

import java.util.AbstractMap;
import java.util.HashMap;

public class Closure implements IClosure<String, String, Boolean> {
    private final Closure parent;
    private final HashMap<String, String> variableTypeMap;
    private final HashMap<String, Boolean> variableValueMap;
    private final HashMap<String, String> methodReturnTypeMap;
    private final HashMap<String, IClosure<String, String, Boolean>> methodClosureMap;
    private final HashMap<String, HashMap<String, String>> methodParametersMap;


    public Closure(Closure parent) {
        this.parent = parent;
        this.variableTypeMap = new HashMap<>();
        this.variableValueMap = new HashMap<>();
        this.methodReturnTypeMap = new HashMap<>();
        this.methodClosureMap = new HashMap<>();
        this.methodParametersMap = new HashMap<>();
    }

    @Override
    public HashMap<String, String> getVariableTypeMap() {
        return variableTypeMap;
    }

    @Override
    public HashMap<String, Boolean> getVariableValueMap() {
        return variableValueMap;
    }

    @Override
    public HashMap<String, String> getMethodReturnTypeMap() {
        return methodReturnTypeMap;
    }

    @Override
    public HashMap<String, IClosure<String, String, Boolean>> getMethodClosureMap() {
        return methodClosureMap;
    }
    @Override
    public IClosure<String, String, Boolean> getChildClosureForMethod(String methodName) {
        // First, check if the method exists in the current closure
        IClosure<String, String, Boolean> childClosure = methodClosureMap.get(methodName);

        // If the method is not found in the current closure, check in children
        if (childClosure == null) {
            for (IClosure<String, String, Boolean> child : methodClosureMap.values()) {
                // Recursively search in the children closures
                childClosure = child.getChildClosureForMethod(methodName);

                // If found in a child closure, break the loop
                if (childClosure != null) {
                    break;
                }
            }
        }

        return childClosure;
    }

    @Override
    public HashMap<String, String> getMethodParameters(String methodName) {
        return methodParametersMap.get(methodName);
    }
    @Override
    public boolean addMethodParameters(String methodName, HashMap<String, String> parameters) {
        boolean itWorked = true;


            if (!methodParametersMap.containsKey(methodName)) {
                // If there are no existing parameters for the method, add the provided parameters
                methodParametersMap.put(methodName, parameters);
            } else {
                // Parameters already exist for the method
                // Check if any of the new parameter names already exist
                for (String paramName : parameters.keySet()) {
                    if (methodParametersMap.get(methodName).containsKey(paramName)) {
                        itWorked = false;
                        break; // Parameter name already exists, exit loop
                    }
                }

                // If all new parameter names are unique, add them
                if (itWorked) {
                    methodParametersMap.get(methodName).putAll(parameters);
                }
            }


        return itWorked;
    }


    public boolean addBoundVariable(String var, String s) {
        boolean itWorked = true;

        // Check if the variable exists in method parameters (including nested HashMaps)
        if (containsKeyInNestedMap(methodParametersMap, var)) {
            itWorked = false;
        } else if (!variableTypeMap.containsKey(var)) {
            // If the variable doesn't exist in the current closure, check the parent closure

            variableTypeMap.put(var, s);



        } else {
            itWorked = false; // Variable already exists in the current closure
        }

        return itWorked;
    }

    // Helper method to check if a key exists in nested HashMaps
    private boolean containsKeyInNestedMap(HashMap<String, HashMap<String, String>> nestedMap, String key) {
        for (HashMap<String, String> innerMap : nestedMap.values()) {
            if (innerMap.containsKey(key)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean addBoundVariableValue(String var, Boolean o) {

        boolean itWorked = true;
        if (variableTypeMap.containsKey(var)) {
            // Variable doesn't exist in the current closure, add its value
            if(!variableValueMap.containsKey(var)){
                variableValueMap.put(var, o);
            } else {

                itWorked = false;
            }

        } else {

            itWorked = false;
        }
        return itWorked;
    }
    @Override
    public void addBoundVariableValueWithoutCheck(String var, Boolean o) {
        variableValueMap.put(var, o);
    }
    @Override
    public boolean addBoundMethod(String methodName, String s, IClosure<String, String, Boolean> closure) {
        boolean itWorked = true;
        if (!methodReturnTypeMap.containsKey(methodName)) {
            // Variable doesn't exist in the current closure, add its value
            methodReturnTypeMap.put(methodName, s);
            // Save the provided closure in the methodClosureMap under the method name
            methodClosureMap.put(methodName, closure);
        } else {

            itWorked = false;
        }
        return itWorked;
    }

    @Override
    // Add this method to update a variable's value
    public boolean updateVariableValue(String var, Boolean newValue) {
        boolean itWorked = true;
        if (variableTypeMap.containsKey(var)) {
            // Variable exists in the current closure, update its value
            variableValueMap.put(var, newValue);
        } else if (parent != null) {
            // Variable not found in the current closure, check parent closures recursively
            itWorked = parent.updateVariableValue(var, newValue);
        } else {

            itWorked = false;
        }
        return itWorked;
    }
    @Override
    public IClosure<String, String, Boolean> getParent() {
        return parent;
    }
    @Override
    public boolean methodExists(String methodName) {
        return methodReturnTypeMap.containsKey(methodName);
    }
    @Override
    public boolean variableExists(String varName) {
        return variableTypeMap.containsKey(varName);
    }
    @Override
    public boolean variableExistsAnywhere(String varName) {
        // Check if the variable exists in the current closure
        if (variableTypeMap.containsKey(varName)) {
            return true;
        }

        // If the variable is not found in the current closure, check parent closures
        if (parent != null) {
            if (parent.variableExistsAnywhere(varName)) {
                return true;
            }
        }

        // If the variable is not found in parent closures, check children closures
        for (IClosure<String, String, Boolean> childClosure : methodClosureMap.values()) {
            if (childClosure.variableExistsAnywhere(varName)) {
                return true;
            }
        }

        // If the variable is not found in any closure, return false
        return false;
    }
    @Override
    public AbstractMap.SimpleEntry<Boolean, String> getVariableTypeAnywhere(String varName) {
        // Check if the variable exists in the current closure
        if (variableTypeMap.containsKey(varName)) {
            return new AbstractMap.SimpleEntry<>(true, variableTypeMap.get(varName));
        }

        // If the variable is not found in the current closure, check in parent closures recursively
        if (parent != null) {
            AbstractMap.SimpleEntry<Boolean, String> parentResult = parent.getVariableTypeAnywhere(varName);

            // If the variable is found in a parent closure, return the result
            if (parentResult.getKey()) {
                return parentResult;
            }
        }

        // If the variable is not found in any closure, return false and null type
        return new AbstractMap.SimpleEntry<>(false, null);
    }


    @Override
    public boolean parameterExists(String methodName, String paramName) {
        // Check if the method exists in the closure
        if (methodParametersMap.containsKey(methodName)) {
            // Check if the parameter exists for the specified method
            HashMap<String, String> parameters = methodParametersMap.get(methodName);
            return parameters.containsKey(paramName);
        }
        return false; // Method or parameter not found
    }

    @Override
    public String getParameterType(String methodName, String paramName) {
        // Check if the method exists in the current closure
        if (methodParametersMap.containsKey(methodName)) {
            // Get the parameter map for the specified method
            HashMap<String, String> parameters = methodParametersMap.get(methodName);

            // Check if the parameter exists in the method's parameter map
            if (parameters.containsKey(paramName)) {
                return parameters.get(paramName); // Return the parameter type
            }
        }

        // If the method or parameter was not found in the current closure, check parent closures
        if (parent != null) {
            String parentParamType = parent.getParameterType(methodName, paramName);
            if (parentParamType != null) {
                return parentParamType; // Return the parameter type from the parent closure
            }
        }

        return null; // Method or parameter not found in the current closure or its parents
    }
    @Override
    public AbstractMap.SimpleEntry<String, Boolean> getVariableTypeAndValue(String varName, boolean checkOnlyBoundVariables) {

        if (checkOnlyBoundVariables && !variableTypeMap.containsKey(varName)) {
            return null; // Variable not found in the current closure
        }

        // Try to retrieve variable type and value from the current closure
        String variableType = variableTypeMap.get(varName);
        Boolean variableValue = variableValueMap.get(varName);

        // If the variable is not found and we're allowed to check parent closures
        if (variableType == null && variableValue == null && parent != null) {
            return parent.getVariableTypeAndValue(varName, false); // Check parent closures
        }

        return new AbstractMap.SimpleEntry<>(variableType, variableValue);
    }

    @Override
    public AbstractMap.SimpleEntry<String, IClosure<String, String, Boolean>> getMethodTypeAndClosure(String methodName, boolean checkOnlyBoundMethods) {

        if (checkOnlyBoundMethods && !methodReturnTypeMap.containsKey(methodName)) {
            return null; // Method not found in the current closure
        }

        // Try to retrieve method return type and closure from the current closure
        String methodReturnType = methodReturnTypeMap.get(methodName);
        IClosure<String, String, Boolean> methodClosure = methodClosureMap.get(methodName);

        // If the method is not found and we're allowed to check parent closures
        if (methodReturnType == null && methodClosure == null && parent != null) {
            return parent.getMethodTypeAndClosure(methodName, false); // Check parent closures
        }

        return new AbstractMap.SimpleEntry<>(methodReturnType, methodClosure);
    }

    @Override
    public IClosure<String, String, Boolean> createNewClosure() {
        return new Closure(this);
    }
}
