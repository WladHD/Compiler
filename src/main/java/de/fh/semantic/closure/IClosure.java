package de.fh.semantic.closure;

import de.fh.Main;
import de.fh.semantic.ComplexParserType;
import de.fh.semantic.err.MethodDeclaredSemanticException;
import de.fh.semantic.err.UnknownSemanticException;
import de.fh.semantic.err.VariableDeclaredSemanticException;
import de.fh.semantic.err.VariableNotDeclaredSemanticException;

import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.HashMap;

public interface IClosure<VarMethodNames, VarMethodType, VarValue> {

    String getClosureName();

    void setClosureName(String name);

    default String getRecursiveClosureName() {
        String current = getClosureName();

        if (getParent() != null) {
            String retrieved = getParent().getRecursiveClosureName();

            if (retrieved != null)
                current = retrieved + "." + current;
        }


        return current;
    }

    HashMap<VarMethodNames, VarMethodType> getVariableTypeMap();

    HashMap<VarMethodNames, VarValue> getVariableValueMap();

    HashMap<VarMethodNames, VarMethodType> getMethodReturnTypeMap();

    HashMap<Integer, VarMethodType> getMethodParams();

    HashMap<VarMethodNames, IClosure<VarMethodNames, VarMethodType, VarValue>> getMethodClosureMap();

    IClosure<VarMethodNames, VarMethodType, VarValue> getParent();

    AbstractMap.SimpleEntry<VarMethodType, VarValue> getVariableTypeAndValue(VarMethodNames varName, boolean checkOnlyBoundVariables);

    AbstractMap.SimpleEntry<VarMethodType, IClosure<VarMethodNames, VarMethodType, VarValue>> getMethodTypeAndClosure(VarMethodNames methodName, boolean checkOnlyBoundMethods);

    IClosure<VarMethodNames, VarMethodType, VarValue> createNewChildClosure(String name);

    default void addVariableDeclaration(VarMethodNames varName, VarMethodType varType) {
        addVariableDeclaration(varName, varType, false, null);
    }

    default void addVariableDeclaration(VarMethodNames varName, VarMethodType varType, boolean isParam, VarValue init) {
        if (hasVariable(varName, true)) {
            throw new VariableDeclaredSemanticException((IClosure<String, ComplexParserType, Object>) this, varName.toString());
        }

        Main.logger.info(MessageFormat.format("[Closure] Added variable declaration {0} with type {1}. Param: {2}", varName, varType, isParam));

        getVariableTypeMap().put(varName, varType);

        if (isParam) {
            getMethodParams().put(getMethodParams().size(), varType);
            addVariableInitialisation(varName, init);
        }
    }

    default void addVariableInitialisation(VarMethodNames varName, VarValue varValue) {
        if (!hasVariable(varName, false))
            throw new VariableNotDeclaredSemanticException((IClosure<String, ComplexParserType, Object>) this, varName.toString());

        Main.logger.info(MessageFormat.format("[Closure] Added variable initialisation {0} with value {1}.", varName, varValue));

        IClosure<VarMethodNames, VarMethodType, VarValue> varHolder = this;

        while(varHolder != null && !varHolder.hasVariable(varName, true)) {
            varHolder = varHolder.getParent();
        }

        // SHOULD NEVER BE POSSIBLE BECAUSE OF CHECK IN BEGINNING ...
        if(varHolder == null)
            throw new UnknownSemanticException("Variablendeklaration konnte nicht gefunden werden, obwohl sie vorliegen müsste ...");

        varHolder.getVariableValueMap().put(varName, varValue);
    }

    default IClosure<VarMethodNames, VarMethodType, VarValue> addMethod(VarMethodNames methodName, VarMethodType methodType) {
        if (hasMethod(methodName, false))
            throw new MethodDeclaredSemanticException((IClosure<String, ComplexParserType, Object>) this, methodName.toString());

        getMethodClosureMap().put(methodName, createNewChildClosure(methodName + "()"));
        getMethodReturnTypeMap().put(methodName, methodType);

        return getMethodClosureMap().get(methodName);
    }

    default boolean hasVariable(VarMethodNames varName, boolean checkOnlyBoundVariables) {
        boolean local = getVariableTypeMap().containsKey(varName);

        if (local || checkOnlyBoundVariables || getParent() == null)
            return local;

        return getParent().hasVariable(varName, false);
    }

    default boolean hasMethod(VarMethodNames varName, boolean checkOnlyBoundMethods) {
        boolean local = getMethodReturnTypeMap().containsKey(varName);

        if (local || checkOnlyBoundMethods || getParent() == null)
            return local;

        return getParent().hasMethod(varName, false);
    }

    AbstractMap.SimpleEntry<VarMethodType,VarValue> getVariableTypeAndValue(VarMethodNames varName, boolean b, IClosure<VarMethodNames, VarMethodType, VarValue> startingClosure);

    AbstractMap.SimpleEntry<VarMethodType, IClosure<VarMethodNames,VarMethodType,VarValue>> getMethodTypeAndClosure(VarMethodNames methodName, boolean b, IClosure<VarMethodNames, VarMethodType, VarValue> startingClosure);
}
