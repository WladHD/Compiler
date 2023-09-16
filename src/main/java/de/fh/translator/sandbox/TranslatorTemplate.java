package de.fh.translator.sandbox;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class TranslatorTemplate {

    public static void main(String[] args) {
        Method mainMethod;

        try {
            mainMethod = Wrapper.class.getDeclaredMethod("main");
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

    public static class Set<T> {
    }

    public static class Map<TA, TB> {

        public Map() {

        }

        public Map(Pair<TA, TB>... pairs) {

        }
    }

    public static class Pair<TA, TB> {
        TA ob1;
        TB ob2;

        public Pair(TA ob1, TB ob2) {
            this.ob1 = ob1;
            this.ob2 = ob2;
        }

        public TA getKey() {
            return ob1;
        }

        public TB getValue() {
            return ob2;
        }
    }

    // @author https://www.baeldung.com/java-copy-directory
    public static class PathUtils {
        private static void copyDirectory(File sourceDirectory, File destinationDirectory) throws IOException {
            if (!destinationDirectory.exists()) {
                destinationDirectory.mkdir();
            }
            for (String f : sourceDirectory.list()) {
                copyDirectoryCompatibityMode(new File(sourceDirectory, f), new File(destinationDirectory, f));
            }
        }

        public static void copyDirectoryCompatibityMode(File source, File destination) throws IOException {
            if (source.isDirectory()) {
                copyDirectory(source, destination);
            } else {
                copyFile(source, destination);
            }
        }

        private static void copyFile(File sourceFile, File destinationFile)
                throws IOException {
            try (InputStream in = new FileInputStream(sourceFile);
                 OutputStream out = new FileOutputStream(destinationFile)) {
                byte[] buf = new byte[1024];
                int length;
                while ((length = in.read(buf)) > 0) {
                    out.write(buf, 0, length);
                }
            }
        }

        public static boolean deleteDirectory(File directoryToBeDeleted) {
            File[] allContents = directoryToBeDeleted.listFiles();
            if (allContents != null) {
                for (File file : allContents) {
                    deleteDirectory(file);
                }
            }
            return directoryToBeDeleted.delete();
        }
    }

    // TODO test
    public static class Path {
        public Files[] files;
        public String[] name;
        private java.io.File current;
        private final String currentPath;

        public Path(String s) {
            this.currentPath = s;
            update();
        }

        private static boolean ask() {
            System.out.println("Es soll eine Verzeichnisoperation durchgeführt werden. Fortfahren? [Y, J / N]");
            String s = new Scanner(System.in).nextLine();
            return s.equalsIgnoreCase("Y") || s.equalsIgnoreCase("J");
        }

        private void update() {
            current = new java.io.File(currentPath);

            if (!current.exists()) {
                files = new Files[0];
                name = new String[0];
                return;
            }

            if (current.isFile()) {
                name = new String[]{current.getAbsolutePath()};
                files = new Files[]{new Files(this, current)};
            } else if (current.isDirectory()) {
                ArrayList<Files> locFil = new ArrayList<>();
                ArrayList<String> locNames = new ArrayList<>();

                for (java.io.File f : Objects.requireNonNull(current.listFiles())) {
                    locFil.add(new Files(new Path(f.getAbsolutePath()), f));
                    locNames.add(f.getAbsolutePath());
                }

                files = new Files[locFil.size()];
                locFil.toArray(files);

                name = new String[locNames.size()];
                locNames.toArray(name);
            }
        }

        public void remove() {
            System.out.println("Removing path " + this);
            System.out.println("REMOVING: " + current.getAbsolutePath());
            if (ask()) {
                PathUtils.deleteDirectory(current);
            }


            update();
        }

        public void copyTo(Path p) {
            System.out.println("Copy path " + this + " to " + p);

            try {
                System.out.println("COPY FROM " + current.getAbsolutePath() + " TO " + p.current.getAbsolutePath());
                if (ask())
                    PathUtils.copyDirectoryCompatibityMode(current, p.current);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void moveTo(Path p) {
            System.out.println("Move path " + this + " to " + p);
            copyTo(p);
            remove();
        }

        @Override
        public String toString() {
            return MessageFormat.format("[PATH(FILES:{0}, NAMES:{1}]", files.length, name.length);
        }
    }

    // TODO test
    public static class Files {
        public String type;
        public String name;
        public Path path;
        private java.io.File current;

        public Files(Path path, java.io.File f) {
            this.path = path;
            this.current = f;
            update();
        }

        public void update() {
            this.type = current.isFile() ? "file" : "dir";
            this.name = current.getName();
        }

        public void remove() {
            System.out.println("Removing " + this);
            path.remove();
        }

        public void rename(String s) {
            System.out.println("Renaming " + this);
            path.moveTo(new Path(current.getParent() + File.separator + s));
        }

        public void moveTo(Path p) {
            System.out.println("Moving " + this + " to " + p);
            path.moveTo(p);
        }

        public String getContent() {
            return "CONTENT " + name;
        }

        @Override
        public String toString() {
            return MessageFormat.format("[FILE(T:{0}, N:{1}, P:{2}]", type, name, path);
        }
    }

}
