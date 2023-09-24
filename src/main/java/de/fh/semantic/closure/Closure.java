package de.fh.semantic.closure;

import de.fh.semantic.err.MethodNotDeclaredSemanticException;
import de.fh.semantic.err.VariableNotDeclaredSemanticException;

import java.util.AbstractMap;
import java.util.HashMap;

public class Closure<VarMethodNames, VarMethodType, VarValue> implements IClosure<VarMethodNames, VarMethodType, VarValue> {
    private final HashMap<VarMethodNames, VarMethodType> hashMapVariableType;
    private final HashMap<VarMethodNames, VarValue> hashMapVariableValue;

    private final HashMap<Integer, VarMethodType> hashMapMethodParam;

    private final HashMap<VarMethodNames, VarMethodType> hashMapMethodType;

    private final HashMap<VarMethodNames, IClosure<VarMethodNames, VarMethodType, VarValue>> hashMapMethodClosure;

    private final IClosure<VarMethodNames, VarMethodType, VarValue> parent;

    public Closure(IClosure<VarMethodNames, VarMethodType, VarValue> parent) {
        this.parent = parent;

        hashMapVariableType = new HashMap<>();
        hashMapVariableValue = new HashMap<>();
        hashMapMethodType = new HashMap<>();
        hashMapMethodParam = new HashMap<>();
        hashMapMethodClosure = new HashMap<>();
    }

    @Override
    public HashMap<VarMethodNames, VarMethodType> getVariableTypeMap() {
        return hashMapVariableType;
    }

    @Override
    public HashMap<VarMethodNames, VarValue> getVariableValueMap() {
        return hashMapVariableValue;
    }

    @Override
    public HashMap<VarMethodNames, VarMethodType> getMethodReturnTypeMap() {
        return hashMapMethodType;
    }

    @Override
    public HashMap<Integer, VarMethodType> getMethodParams() {
        return hashMapMethodParam;
    }

    @Override
    public HashMap<VarMethodNames, IClosure<VarMethodNames, VarMethodType, VarValue>> getMethodClosureMap() {
        return hashMapMethodClosure;
    }

    @Override
    public IClosure<VarMethodNames, VarMethodType, VarValue> getParent() {
        return parent;
    }

    @Override
    public AbstractMap.SimpleEntry<VarMethodType, VarValue> getVariableTypeAndValue(VarMethodNames varName, boolean checkOnlyBoundVariables) {
        boolean local = getVariableTypeMap().containsKey(varName);

        if (local || checkOnlyBoundVariables || getParent() == null) {
            if(!local)
                throw new VariableNotDeclaredSemanticException(varName.toString());

            return new AbstractMap.SimpleEntry<>(getVariableTypeMap().get(varName), getVariableValueMap().getOrDefault(varName, null));
        }

        return getParent().getVariableTypeAndValue(varName, false);
    }

    @Override
    public AbstractMap.SimpleEntry<VarMethodType, IClosure<VarMethodNames, VarMethodType, VarValue>> getMethodTypeAndClosure(VarMethodNames methodName, boolean checkOnlyBoundMethods) {
        boolean local = getMethodReturnTypeMap().containsKey(methodName);

        if (local || checkOnlyBoundMethods || getParent() == null) {
            if(!local)
                throw new MethodNotDeclaredSemanticException(methodName.toString());

            return new AbstractMap.SimpleEntry<>(getMethodReturnTypeMap().get(methodName), getMethodClosureMap().get(methodName));
        }

        return getParent().getMethodTypeAndClosure(methodName, false);
    }

    @Override
    public IClosure<VarMethodNames, VarMethodType, VarValue> createNewChildClosure() {
        return new Closure<>(this);
    }
}
