package de.fh.semantic;

public enum ParserTypes {
    INT("int", "Integer", true),
    BOOLEAN("boolean", "Boolean", true),
    CHAR("char", "Char", true),
    EMPTY_ARRAY_CONTAINER("EMPTY_ARRAY_CONTAINER", false),

    NATIVE_ARRAY("NATIVE_ARRAY", false),
    SET_ARRAY("SET_ARRAY", false),
    MAP_ARRAY("MAP_ARRAY", false),
    PATH("Path"),
    STRING("String"),
    MAP("Map"),
    SET("Set"),
    FILES("Files");

    private final String parserType;
    private final String javaType;

    private final boolean isReachable;

    ParserTypes(String s) {
        this(s, true);
    }

    ParserTypes(String s, boolean isReachable) {
        this(s, null, isReachable);
    }

    ParserTypes(String s, String java, boolean isReachable) {
        parserType = s;

        if (java == null)
            javaType = s;
        else
            javaType = java;

        this.isReachable = isReachable;
    }

    public static ParserTypes match(String unknownType) {
        for (ParserTypes pt : values())
            if (pt.parserType.equals(unknownType) && pt.isReachable)
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
