package de.fh.semantic.closure;

import java.util.AbstractMap;
import java.util.HashMap;

public class Closure implements IClosure<String, String, Object> {
    @Override
    public HashMap<String, String> getVariableTypeMap() {
        return null;
    }

    @Override
    public HashMap<String, Object> getVariableValueMap() {
        return null;
    }

    @Override
    public HashMap<String, String> getMethodReturnTypeMap() {
        return null;
    }

    @Override
    public HashMap<String, IClosure<String, String, Object>> getMethodClosureMap() {
        return null;
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
        return null;
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
        return null;
    }
}
