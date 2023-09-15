package de.fh.semantic.closure;

import java.util.AbstractMap;
import java.util.HashMap;

public interface IClosure<VarMethodNames, VarMethodType, VarValue> {

    HashMap<VarMethodNames, VarMethodType> getVariableTypeMap();

    HashMap<VarMethodNames, VarValue> getVariableValueMap();

    HashMap<VarMethodNames, VarMethodType> getMethodReturnTypeMap();

    HashMap<VarMethodNames, IClosure<VarMethodNames, VarMethodType, VarValue>> getMethodClosureMap();

    IClosure<String, String, Boolean> getChildClosureForMethod(String methodName);

    HashMap<String, String> getMethodParameters(String methodName);

    boolean addMethodParameters(String methodName, HashMap<String, String> parameters);

    boolean addBoundVariable(VarMethodNames var, VarMethodType type);

    boolean addBoundVariableValue(VarMethodNames var, VarValue value);

    boolean addBoundMethod(VarMethodNames methodName, VarMethodType type, IClosure<VarMethodNames, VarMethodType, VarValue> closure);

    // Add this method to update a variable's value
    boolean updateVariableValue(VarMethodNames var, VarValue newValue);

    IClosure<VarMethodNames, VarMethodType, VarValue> getParent();

    AbstractMap.SimpleEntry<VarMethodType, VarValue> getVariableTypeAndValue(VarMethodNames varName, boolean checkOnlyBoundVariables);

    AbstractMap.SimpleEntry<VarMethodType, IClosure<VarMethodNames, VarMethodType, VarValue>> getMethodTypeAndClosure(VarMethodNames methodName, boolean checkOnlyBoundMethods);

    IClosure<VarMethodNames, VarMethodType, VarValue> createNewClosure();

    default boolean hasVariable(VarMethodNames varName, boolean checkOnlyBoundVariables) {
        boolean local = getVariableTypeMap().containsKey(varName);

        if (checkOnlyBoundVariables)
            return local;

        return getParent().hasVariable(varName, false);
    }

    default boolean hasMethod(VarMethodNames varName, boolean checkOnlyBoundMethods) {
        boolean local = getMethodReturnTypeMap().containsKey(varName);

        if (checkOnlyBoundMethods)
            return local;

        return getParent().hasMethod(varName, false);
    }


}
