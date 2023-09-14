package de.fh.translator.sandbox;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TranslatorTemplate {

    public static void main(String[] args) {
        Method mainMethod;

        try {
            mainMethod = Wrapper.class.getMethod("main");
        } catch (NoSuchMethodException e) {
            System.out.println("Es fehlt die main() Methode im Source Code");
            throw new RuntimeException(e);
        }

        Wrapper wrapper = new Wrapper();
        try {
            mainMethod.invoke(wrapper);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public interface IOpOverload {
        Object p11(char op, Object a, Object b);
    }

    public static class Wrapper {
        // GENERATED CODE
        /* PLACEHOLDER */
    }

    public static class Set<T> implements IOpOverload {

        @Override
        public Object p11(char op, Object a, Object b) {
            if(!(a instanceof Set) || !(b instanceof Set) )
                throw new RuntimeException("Wrong overload ...");

            return new Set<T>();
        }
    }

    public static class Map<TA, TB> {

    }

    // TODO test
    public static class Path {
        public Files[] files;
        public String[] name;

        public Path(String s) {
            File jfile = new File(s);

            if(!jfile.exists()) {
                files = new Files[0];
                name = new String[0];
                return;
            }

            if (jfile.isDirectory()) {
                ArrayList<Files> locFil = new ArrayList<>();
                ArrayList<String> locNames = new ArrayList<>();
                for (java.io.File f : Objects.requireNonNull(jfile.listFiles())) {
                    locFil.add(new Files(new Path(f.getAbsolutePath())));
                    locNames.add(f.getAbsolutePath());
                }
                files = new Files[locFil.size()];
                locFil.toArray(files);

                name = new String[locNames.size()];
                locNames.toArray(name);
                return;
            }

            if (jfile.isFile()) {
                files = new Files[] { new Files(this) };
                name = new String[] { jfile.getAbsolutePath() };
            }
        }

        public void remove() {
            System.out.println("Removing path " + this);
        }

        public void copyTo(Path p) {
            System.out.println("Copy path " + this + " to " + p);
        }

        public void moveTo(Path p) {
            System.out.println("Move path " + this + " to " + p);
        }

        @Override
        public String toString() {
            return MessageFormat.format("[PATH(F:{0}, N:{1}]", files.length, name.length);
        }
    }

    // TODO test
    public static class Files {
        public String type;
        public String name;
        public Path path;

        public Files(Path path) {
            this.path = path;
        }

        public void remove() {
            System.out.println("Removing " + this);
        }

        public void rename(String s) {
            System.out.println("Renaming " + this);
        }

        public void moveTo(Path p) {
            System.out.println("Moving " + this + " to " + p);
        }

        public String getContent() {
            return "CONTENT " + name;
        }

        @Override
        public String toString() {
            return MessageFormat.format("[FILE(T:{0}, N:{1}, P:{2}]", type, name, path);
        }
    }

    public static class OpOverload {
        public static Number p11(char operator, Number a, Number b) {

            if (a instanceof Double || b instanceof Double) {
                switch (operator) {
                    case '+' -> {
                        return a.doubleValue() + b.doubleValue();
                    }
                    case '-' -> {
                        return a.doubleValue() - b.doubleValue();
                    }
                }
            }


            if (a instanceof Float || b instanceof Float) {
                switch (operator) {
                    case '+' -> {
                        return a.floatValue() + b.floatValue();
                    }
                    case '-' -> {
                        return a.floatValue() - b.floatValue();
                    }
                }
            }

            if (a instanceof Long || b instanceof Long)
                switch (operator) {
                    case '+' -> {
                        return a.longValue() + b.longValue();
                    }
                    case '-' -> {
                        return a.longValue() - b.longValue();
                    }
                }

            switch (operator) {
                case '+' -> {
                    return a.intValue() + b.intValue();
                }
                case '-' -> {
                    return a.intValue() - b.intValue();
                }
            }

            throw new RuntimeException("Operator Overload Prio 11 failed ...");
        }

        public static Object p11(char op, Object a, Object b) {
            if (a instanceof Number && b instanceof Number)
                return p11(op, (Number) a, (Number) b);

            if (a instanceof IOpOverload ov)
                return ov.p11(op, a, b);

            if (b instanceof IOpOverload ov)
                return ov.p11(op, a, b);

            throw new RuntimeException("Couldn't definition for overload ...");
        }
    }

}
