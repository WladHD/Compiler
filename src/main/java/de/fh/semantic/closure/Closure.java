package de.fh.semantic.closure;

import de.fh.semantic.ComplexParserType;
import de.fh.semantic.err.MethodNotDeclaredSemanticException;
import de.fh.semantic.err.VariableNotDeclaredSemanticException;

import java.util.AbstractMap;
import java.util.HashMap;

public class Closure<VarMethodNames, VarMethodType, VarValue> implements IClosure<VarMethodNames, VarMethodType, VarValue> {
    private final HashMap<VarMethodNames, VarMethodType> hashMapVariableType;
    private final HashMap<VarMethodNames, VarValue> hashMapVariableValue;

    private final HashMap<Integer, VarMethodType> hashMapMethodParam;

    private String closureName;

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
    public String getClosureName() {
        return closureName;
    }

    @Override
    public void setClosureName(String name) {
        closureName = name;
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
        return getVariableTypeAndValue(varName, checkOnlyBoundVariables, this);
    }

    @Override
    public AbstractMap.SimpleEntry<VarMethodType, VarValue> getVariableTypeAndValue(VarMethodNames varName, boolean checkOnlyBoundVariables, IClosure<VarMethodNames, VarMethodType, VarValue> startingClosure) {
        boolean local = getVariableTypeMap().containsKey(varName);

        if (local || checkOnlyBoundVariables || getParent() == null) {
            if(!local)
                throw new VariableNotDeclaredSemanticException((IClosure<String, ComplexParserType, Object>) startingClosure, varName.toString());

            return new AbstractMap.SimpleEntry<>(getVariableTypeMap().get(varName), getVariableValueMap().getOrDefault(varName, null));
        }

        return getParent().getVariableTypeAndValue(varName, false, startingClosure);
    }

    @Override
    public AbstractMap.SimpleEntry<VarMethodType, IClosure<VarMethodNames, VarMethodType, VarValue>> getMethodTypeAndClosure(VarMethodNames methodName, boolean checkOnlyBoundMethods) {
        return getMethodTypeAndClosure(methodName, checkOnlyBoundMethods, this);
    }

    @Override
    public AbstractMap.SimpleEntry<VarMethodType, IClosure<VarMethodNames, VarMethodType, VarValue>> getMethodTypeAndClosure(VarMethodNames methodName, boolean checkOnlyBoundMethods, IClosure<VarMethodNames, VarMethodType, VarValue> startingClosure) {
        boolean local = getMethodReturnTypeMap().containsKey(methodName);

        if (local || checkOnlyBoundMethods || getParent() == null) {
            if(!local)
                throw new MethodNotDeclaredSemanticException((IClosure<String, ComplexParserType, Object>) startingClosure, methodName.toString());

            return new AbstractMap.SimpleEntry<>(getMethodReturnTypeMap().get(methodName), getMethodClosureMap().get(methodName));
        }

        return getParent().getMethodTypeAndClosure(methodName, false, startingClosure);
    }

    @Override
    public IClosure<VarMethodNames, VarMethodType, VarValue> createNewChildClosure(String name) {
        Closure<VarMethodNames, VarMethodType, VarValue> c = new Closure<>(this);
        c.setClosureName(name);

        return c;
    }
}
