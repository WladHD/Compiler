package de.fh.semantic;

public enum ParserTypes {
    INT("int", "Integer"),
    BOOLEAN("boolean", "Boolean"),
    CHAR("char", "Char"),
    PATH("Path"),
    STRING("String"),
    MAP("Map"),
    SET("Path"),
    FILES("Files");

    private final String parserType;
    private final String javaType;

    ParserTypes(String s) {
        this(s, null);
    }

    ParserTypes(String s, String java) {
        parserType = s;

        if (java == null)
            javaType = s;
        else
            javaType = java;
    }

    public static ParserTypes match(String unknownType) {
        for (ParserTypes pt : values())
            if (pt.parserType.equals(unknownType))
                return pt;

        return null;
    }

    public String getParserType() {
        return parserType;
    }

    public String getJavaType() {
        return javaType;
    }

    public String toString() {
        return getParserType();
    }
}
