package de.fh.semantic;

import java.util.ArrayList;

public class ComplexParserType implements Cloneable {
    private ParserTypes basicType;

    private ArrayList<ComplexParserType> complexParserTypes;

    private boolean isArray = false;

    public ComplexParserType(ParserTypes pt) {
        setBasicType(pt);
    }

    public static boolean isEqual(Object a, Object b) {
        if (a instanceof ComplexParserType aC)
            return aC.isEqual(b);

        if (b instanceof ComplexParserType bC)
            return bC.isEqual(a);

        if (a instanceof ParserTypes aP && b instanceof ParserTypes bP)
            return aP.equals(bP);

        return false;
    }

    public boolean isArray() {
        return isArray;
    }

    public void setArray(boolean isArray) {
        this.isArray = isArray;
    }

    public ParserTypes getBasicType() {
        return basicType;
    }

    public void setBasicType(ParserTypes pt) {
        basicType = pt;
    }

    public void addComplexType(ComplexParserType cpt) {
        if (cpt == null)
            return;

        if (complexParserTypes == null)
            complexParserTypes = new ArrayList<>();

        complexParserTypes.add(cpt);
    }

    public ArrayList<ComplexParserType> getComplexParserTypes() {
        return complexParserTypes;
    }

    public boolean hasComplexParserTypes() {
        return complexParserTypes != null;
    }

    public boolean isEqual(Object o) {
        // OBJECT ACCEPTS ALL
        if(getBasicType() == ParserTypes.OBJECT && o != null)
            return true;

        if (o instanceof ParserTypes pt && !hasComplexParserTypes()) {
            return pt == getBasicType();
        }

        if(getBasicType() == ParserTypes.SET || getBasicType() == ParserTypes.MAP || isArray)
            return ComplexParserTypeIdentifier.acceptArrayValue(this, o);

        if (!this.getClass().isInstance(o))
            return false;

        return toString().equals(o.toString());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(basicType.getParserType());

        if (complexParserTypes != null) {
            sb.append("<");

            for (int i = 0; i < complexParserTypes.size(); i++) {
                if (i != 0)
                    sb.append(", ");

                sb.append(complexParserTypes.get(i).toString());
            }

            sb.append(">");
        }

        if (isArray)
            sb.append("[]");

        return sb.toString();
    }

    public String toStringJava(boolean displayPrimitive) {
        StringBuilder sb = new StringBuilder();
        sb.append(displayPrimitive ? basicType.getParserType() : basicType.getJavaType());

        if (complexParserTypes != null) {
            sb.append("<");

            for (int i = 0; i < complexParserTypes.size(); i++) {
                if (i != 0)
                    sb.append(", ");

                sb.append(complexParserTypes.get(i).toStringJava(false));
            }

            sb.append(">");
        }

        if (isArray)
            sb.append("[]");

        return sb.toString();
    }

    @Override
    public ComplexParserType clone() {
        ComplexParserType clone = new ComplexParserType(getBasicType());

        if(complexParserTypes != null)
            for(int i = 0; i < complexParserTypes.size(); i++)
                clone.addComplexType(complexParserTypes.get(i).clone());

        clone.isArray = isArray;

        return clone;
    }
}