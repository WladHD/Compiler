package de.fh.semantic;

import de.fh.semantic.err.ExpectedTypeMissmatchSemanticException;
import de.fh.semantic.err.IllegalOperationSemanticException;

import javax.swing.text.html.parser.Parser;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class ComplexParserTypeIdentifier {

    static TypeMaps[] tm = new TypeMaps[]{
            new TypeMaps(ParserTypes.STRING, ParserTypes.STRING, ParserTypes.values(), new String[]{"+"}),
            new TypeMaps(ParserTypes.SET, ParserTypes.SET, new ParserTypes[]{
                    ParserTypes.SET
            }, new String[]{"+", "-", "^"}),
            new TypeMaps(ParserTypes.BOOLEAN, null, ParserTypes.values(), new String[]{"==", "!="}),
            new TypeMaps(ParserTypes.BOOLEAN, ParserTypes.BOOLEAN, new ParserTypes[]{ParserTypes.BOOLEAN}, new String[]{
                    "||", "&&"
            }),
            new TypeMaps(ParserTypes.INT, null, new ParserTypes[]{
                    ParserTypes.INT, ParserTypes.CHAR
            }, new String[]{"+", "-", "*", "/", "%"})
    };

    public static ComplexParserType inferDatatype(ComplexParserType a, ComplexParserType b, String operator) {
        for (TypeMaps tmm : tm) {
            // check if operator match
            if (!Arrays.asList(tmm.allowedOperators).contains(operator))
                continue;

            // check if required type is present
            if (tmm.requires != null && a.getBasicType() != tmm.requires && b.getBasicType() != tmm.requires)
                continue;

            ComplexParserType required = a.getBasicType() == tmm.requires ? a : b;
            ComplexParserType second = a.getBasicType() == tmm.requires ? b : a;

            boolean containsType = Arrays.stream(tmm.anything).anyMatch(parserTypes -> parserTypes.equals(second.getBasicType()));

            if (!containsType)
                continue;

            return required;
        }

        throw new IllegalOperationSemanticException(a, b, operator);
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
        }

        return true;
    }

    private static boolean acceptArrayValueMap(ComplexParserType declared, Object arrayOrEmptyArray) {
        if (!declared.hasComplexParserTypes() && declared.getComplexParserTypes().size() != 2)
            return false;

        ComplexParserType declaredTypeKey = declared.getComplexParserTypes().get(0);
        ComplexParserType declaredTypeValue = declared.getComplexParserTypes().get(1);

        if (arrayOrEmptyArray instanceof ArrayList<?> arr) {
            for (Object o : arr) {
                if(o instanceof ArrayList<?> pair) {
                    if (!declaredTypeKey.isEqual(pair.get(0))) {
                        return false;
                    }

                    return declaredTypeValue.isEqual(pair.get(1));
                }
            }
        }

        return true;
    }

    public static boolean acceptArrayValue(ComplexParserType declared, Object arrayOrEmptyArray) {
        if (arrayOrEmptyArray instanceof ComplexParserType cpt && cpt.isEqual(ParserTypes.EMPTY_ARRAY_CONTAINER))
            return true;

        if (declared.getBasicType() == ParserTypes.SET)
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

        public TypeMaps(ParserTypes result, ParserTypes requires, ParserTypes[] anything, String[] operators) {
            this.result = result;
            this.requires = requires;
            this.anything = anything;
            this.allowedOperators = operators;
        }
    }

}
