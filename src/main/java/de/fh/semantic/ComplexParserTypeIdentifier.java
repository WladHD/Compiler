package de.fh.semantic;

import de.fh.javacc.generated.SimpleNode;
import de.fh.semantic.closure.IClosure;
import de.fh.semantic.err.IllegalOperationSemanticException;

import java.util.ArrayList;
import java.util.Arrays;

public class ComplexParserTypeIdentifier {

    static TypeMaps[] exp_op_exp = new TypeMaps[]{
            new TypeMaps(ParserTypes.STRING, ParserTypes.STRING, ParserTypes.values(), new String[]{"+"}),
            new TypeMaps(ParserTypes.SET, ParserTypes.SET, new ParserTypes[]{
                    ParserTypes.SET
            }, new String[]{"+", "-", "^"}),
            new TypeMaps(ParserTypes.BOOLEAN, null, ParserTypes.values(), new String[]{"==", "!="}, true, false),
            new TypeMaps(ParserTypes.BOOLEAN, ParserTypes.BOOLEAN, new ParserTypes[]{ParserTypes.BOOLEAN}, new String[]{
                    "||", "&&"
            }),
            new TypeMaps(ParserTypes.BOOLEAN, null, new ParserTypes[]{ParserTypes.INT, ParserTypes.CHAR}, new String[]{
                    ">", "<", ">=", "<="
            }),
            new TypeMaps(ParserTypes.INT, null, new ParserTypes[]{
                    ParserTypes.INT, ParserTypes.CHAR
            }, new String[]{"+", "-", "*", "/", "%"}),

            new TypeMaps(ParserTypes.INT, ParserTypes.INT, new ParserTypes[]{ParserTypes.INT, ParserTypes.CHAR}, new String[]{
                    "+=", "-=", "*=", "/=", "%="
            }, false, true),
            new TypeMaps(ParserTypes.CHAR, ParserTypes.CHAR, new ParserTypes[]{ParserTypes.INT, ParserTypes.CHAR}, new String[]{
                    "+=", "-=", "*=", "/=", "%="
            }, false, true),
            new TypeMaps(ParserTypes.STRING, ParserTypes.STRING, ParserTypes.values(), new String[]{
                    "+="
            }, false, true),
            new TypeMaps(ParserTypes.SAME_TYPE, null, ParserTypes.values(), new String[]{"="}, true, false)
    };

    static TypeMaps[] exp_pre_post = new TypeMaps[]{
            new TypeMaps(ParserTypes.INT, null, new ParserTypes[]{ParserTypes.INT}, new String[]{"++", "--"}, true, false),
            new TypeMaps(ParserTypes.INT, null, new ParserTypes[]{ParserTypes.INT}, new String[]{"+", "-"}),
            new TypeMaps(ParserTypes.BOOLEAN, null, new ParserTypes[]{ParserTypes.BOOLEAN}, new String[]{"!"})
    };

    public static ComplexParserType inferDatatypeFromUnaryOperation(IClosure<String, ComplexParserType, Object> closure, ComplexParserType a, String operator, SimpleNode child) {
        for (TypeMaps tmm : exp_pre_post) {
            // check if operator match
            if (!Arrays.asList(tmm.allowedOperators).contains(operator))
                continue;

            boolean containsType = Arrays.stream(tmm.anything).anyMatch(parserTypes -> parserTypes.equals(a.getBasicType()));

            if (!containsType)
                continue;

            // TODO check if SimpleNode can be incremented
            // if(tmm.requiresSameType && !(child instanceof ASTLITERAL_IDENTIFIER))
            //    continue;

            return a;
        }

        throw new IllegalOperationSemanticException(closure, null, a, operator);
    }

    public static ComplexParserType inferDatatypeFromOperation(IClosure<String, ComplexParserType, Object> closure, ComplexParserType a, ComplexParserType b, String operator) {
        for (TypeMaps tmm : exp_op_exp) {
            // SPECIAL CASE

            if(Arrays.asList(tmm.allowedOperators).contains("=") && tmm.result == ParserTypes.SAME_TYPE) {
                if(b.getBasicType() == ParserTypes.EMPTY_ARRAY_CONTAINER &&
                        (a.getBasicType() == ParserTypes.SET || a.getBasicType() == ParserTypes.MAP || a.isArray())) {
                    return a;
                }
            }

            // check if operator match
            if (!Arrays.asList(tmm.allowedOperators).contains(operator))
                continue;

            // check if required type is present
            if (tmm.requires != null && a.getBasicType() != tmm.requires && b.getBasicType() != tmm.requires)
                continue;

            if (tmm.requiredTypeMustBeLeftSide && a.getBasicType() != tmm.requires)
                continue;

            ComplexParserType required = a.getBasicType() == tmm.requires ? a : b;
            ComplexParserType second = a.getBasicType() == tmm.requires ? b : a;

            boolean containsType = Arrays.stream(tmm.anything).anyMatch(parserTypes -> parserTypes.equals(second.getBasicType()));

            if (!containsType)
                continue;

            if (tmm.requiresSameType && !required.isEqual(second))
                continue;

            if (required.isEqual(ParserTypes.SAME_TYPE))
                return required;

            if(required.getBasicType() == ParserTypes.SET)
                if(!required.toString().equals(second.toString()))
                    continue;

            required.setBasicType(tmm.result);

            return required;
        }

        throw new IllegalOperationSemanticException(closure, a, b, operator);
    }

    private static boolean acceptArrayValueSet(ComplexParserType declared, Object arrayOrEmptyArray) {
        if (!declared.hasComplexParserTypes() && declared.getComplexParserTypes().size() != 1)
            return false;

        if (arrayOrEmptyArray instanceof ArrayList<?> arr) {
            for (Object o : arr) {
                if (o instanceof ComplexParserType cpt && !declared.getComplexParserTypes().get(0).isEqual(cpt))
                    return false;

                if (o instanceof ArrayList<?> arr2 && !acceptArrayValue(declared.getComplexParserTypes().get(0), arr2))
                    return false;
            }

            return true;
        }

        return arrayOrEmptyArray instanceof ComplexParserType cpt && cpt.toString().equals(declared.toString());
    }

    private static boolean acceptArrayValueNativeArray(ComplexParserType declared, Object arrayOrEmptyArray) {
        if (arrayOrEmptyArray instanceof ArrayList<?> arr) {
            ComplexParserType clonedDeclared = declared.clone();
            clonedDeclared.setArray(false);

            for (Object o : arr) {
                if (!clonedDeclared.isEqual(o)) {
                    return false;
                }
            }

            return true;
        }

        return arrayOrEmptyArray instanceof ComplexParserType cpt && cpt.toString().equals(declared.toString());
    }

    private static boolean acceptArrayValueMap(ComplexParserType declared, Object arrayOrEmptyArray) {
        if (!declared.hasComplexParserTypes() && declared.getComplexParserTypes().size() != 2)
            return false;

        ComplexParserType declaredTypeKey = declared.getComplexParserTypes().get(0);
        ComplexParserType declaredTypeValue = declared.getComplexParserTypes().get(1);

        if (arrayOrEmptyArray instanceof ArrayList<?> arr) {
            for (Object o : arr) {
                if (o instanceof ArrayList<?> pair) {
                    if (!declaredTypeKey.isEqual(pair.get(0))) {
                        return false;
                    }

                    return declaredTypeValue.isEqual(pair.get(1));
                }
            }

            return true;
        }

        return arrayOrEmptyArray instanceof ComplexParserType cpt && cpt.toString().equals(declared.toString());
    }

    public static boolean acceptArrayValue(ComplexParserType declared, Object arrayOrEmptyArray) {
        if (arrayOrEmptyArray instanceof ComplexParserType cpt && cpt.isEqual(ParserTypes.EMPTY_ARRAY_CONTAINER))
            return true;

        if (declared.isArray())
            return acceptArrayValueNativeArray(declared, arrayOrEmptyArray);
        else if (declared.getBasicType() == ParserTypes.SET)
            return acceptArrayValueSet(declared, arrayOrEmptyArray);
        else if (declared.getBasicType() == ParserTypes.MAP)
            return acceptArrayValueMap(declared, arrayOrEmptyArray);

        return false;
    }

    public static class TypeMaps {
        ParserTypes result;
        ParserTypes requires;
        ParserTypes[] anything;
        String[] allowedOperators;

        boolean requiresSameType;
        boolean requiredTypeMustBeLeftSide;

        public TypeMaps(ParserTypes result, ParserTypes requires, ParserTypes[] anything, String[] operators) {
            this(result, requires, anything, operators, false, false);
        }

        public TypeMaps(ParserTypes result, ParserTypes requires, ParserTypes[] anything, String[] operators, boolean requiresSameType, boolean requiredTypeMustBeLeftSide) {
            this.result = result;
            this.requires = requires;
            this.anything = anything;
            this.allowedOperators = operators;
            this.requiresSameType = requiresSameType;
            this.requiredTypeMustBeLeftSide = requiredTypeMustBeLeftSide;
        }
    }

}
