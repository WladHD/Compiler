package de.fh.semantic.closure;

import de.fh.semantic.err.MethodDeclaredSemanticException;
import de.fh.semantic.err.VariableDeclaredSemanticException;
import de.fh.semantic.err.VariableNotDeclaredSemanticException;

import java.util.AbstractMap;
import java.util.HashMap;

public interface IClosure<VarMethodNames, VarMethodType, VarValue> {

    HashMap<VarMethodNames, VarMethodType> getVariableTypeMap();

    HashMap<VarMethodNames, VarValue> getVariableValueMap();

    HashMap<VarMethodNames, VarMethodType> getMethodReturnTypeMap();

    HashMap<VarMethodNames, VarMethodType> getMethodParamMap();

    HashMap<VarMethodNames, IClosure<VarMethodNames, VarMethodType, VarValue>> getMethodClosureMap();

    IClosure<VarMethodNames, VarMethodType, VarValue> getParent();

    AbstractMap.SimpleEntry<VarMethodType, VarValue> getVariableTypeAndValue(VarMethodNames varName, boolean checkOnlyBoundVariables);

    AbstractMap.SimpleEntry<VarMethodType, IClosure<VarMethodNames, VarMethodType, VarValue>> getMethodTypeAndClosure(VarMethodNames methodName, boolean checkOnlyBoundMethods);

    IClosure<VarMethodNames, VarMethodType, VarValue> createNewChildClosure();

    default void addVariableDeclaration(VarMethodNames varName, VarMethodType varType, boolean isParam) {
        if (hasVariable(varName, true)) {
            throw new VariableDeclaredSemanticException(varName.toString());
        }

        if (isParam)
            getMethodParamMap().put(varName, varType);


        getVariableTypeMap().put(varName, varType);
    }

    default void addVariableInitialisation(VarMethodNames varName, VarValue varValue) {
        if (!hasVariable(varName, false))
            throw new VariableNotDeclaredSemanticException(varName.toString());

        getVariableValueMap().put(varName, varValue);
    }

    default IClosure<VarMethodNames, VarMethodType, VarValue> addMethod(VarMethodNames methodName, VarMethodType methodType) {
        if (hasMethod(methodName, false))
            throw new MethodDeclaredSemanticException(methodName.toString());

        getMethodClosureMap().put(methodName, createNewChildClosure());
        getMethodReturnTypeMap().put(methodName, methodType);

        return getMethodClosureMap().get(methodName);
    }

    default boolean hasVariable(VarMethodNames varName, boolean checkOnlyBoundVariables) {
        boolean local = getVariableTypeMap().containsKey(varName);

        if (checkOnlyBoundVariables || getParent() == null)
            return local;

        return getParent().hasVariable(varName, false);
    }

    default boolean isVariableParam(VarMethodNames varName) {
        return getMethodParamMap().containsKey(varName);
    }

    default boolean hasMethod(VarMethodNames varName, boolean checkOnlyBoundMethods) {
        boolean local = getMethodReturnTypeMap().containsKey(varName);

        if (checkOnlyBoundMethods || getParent() == null)
            return local;

        return getParent().hasMethod(varName, false);
    }


}
