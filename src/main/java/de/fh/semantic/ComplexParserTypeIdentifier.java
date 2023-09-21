package de.fh.semantic;

import javax.swing.text.html.parser.Parser;
import java.util.Arrays;

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

            if(!containsType)
                continue;

            return required;
        }



        return null;
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
