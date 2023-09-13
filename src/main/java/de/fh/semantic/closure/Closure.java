package de.fh.semantic.closure;

import java.util.AbstractMap;
import java.util.HashMap;

public class Closure implements IClosure<String, String, Boolean> {
    private final Closure parent;
    private final HashMap<String, String> variableTypeMap;
    private final HashMap<String, Boolean> variableValueMap;
    private final HashMap<String, String> methodReturnTypeMap;
    private final HashMap<String, IClosure<String, String, Boolean>> methodClosureMap;

    public Closure(Closure parent) {
        this.parent = parent;
        this.variableTypeMap = new HashMap<>();
        this.variableValueMap = new HashMap<>();
        this.methodReturnTypeMap = new HashMap<>();
        this.methodClosureMap = new HashMap<>();
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
    public void addBoundVariable(String var, String s) {

        variableTypeMap.put(var, s);
    }

    @Override
    public void addBoundVariableValue(String var, Boolean o) {
        variableValueMap.put(var, o);
    }

    @Override
    public void addBoundMethod(String methodName, String s, IClosure<String, String, Boolean> closure) {
        methodReturnTypeMap.put(methodName, s);
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
            parent.updateVariableValue(var, newValue);
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
