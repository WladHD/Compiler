package de.fh.translator.sandbox;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Watchable;

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

    public static class Wrapper {
        // GENERATED CODE
        /* PLACEHOLDER */
    }

    public static class Path {

    }

}
