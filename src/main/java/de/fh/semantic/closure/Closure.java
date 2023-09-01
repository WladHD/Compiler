package de.fh.semantic.closure;

import java.util.AbstractMap;
import java.util.HashMap;

public class Closure implements IClosure<String, String, Object> {
    private final Closure parent;
    private final HashMap<String, String> variableTypeMap;
    private final HashMap<String, Object> variableValueMap;
    private final HashMap<String, String> methodReturnTypeMap;
    private final HashMap<String, IClosure<String, String, Object>> methodClosureMap;

    private Closure(Closure parent) {
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
    public HashMap<String, Object> getVariableValueMap() {
        return variableValueMap;
    }

    @Override
    public HashMap<String, String> getMethodReturnTypeMap() {
        return methodReturnTypeMap;
    }

    @Override
    public HashMap<String, IClosure<String, String, Object>> getMethodClosureMap() {
        return methodClosureMap;
    }

    @Override
    public void addBoundVariable(String var, String s) {

    }

    @Override
    public void addBoundVariableValue(String var, Object o) {

    }

    @Override
    public void addBoundMethod(String methodName, String s, IClosure<String, String, Object> closure) {

    }

    @Override
    public IClosure<String, String, Object> getParent() {
        return parent;
    }

    @Override
    public AbstractMap.SimpleEntry<String, Object> getVariableTypeAndValue(String varName, boolean checkOnlyBoundVariables) {
        return null;
    }

    @Override
    public AbstractMap.SimpleEntry<String, IClosure<String, String, Object>> getMethodTypeAndClosure(String methodName, boolean checkOnlyBoundMethods) {
        return null;
    }

    @Override
    public IClosure<String, String, Object> createNewClosure() {
        return new Closure(this);
    }
}
