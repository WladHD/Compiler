## Steps
1. Dependencies von pom.xml installieren (automatisch mit IntelliJ)
2. JavaCC IntelliJ Plugin installieren https://plugins.jetbrains.com/plugin/11431-javacc

## JavaCC Grammatik Kompilieren
1. Grammatik \*.jjt unter de.fh.javacc speichern
2. ``mvn generate-sources`` ausführen
3. Generierte Grammatik wird unter de.fh.javacc.generated(.ast) gespeichert

## Programm Nutzen
1. Quellcode in Textdatei schreiben (z. B. helloWorld.fare)
2. Compiler aufrufen ``java -jar FARECompiler.jar <Source> [Target]``
3. `Source` ist ein benötigtes Argument für den Pfad zum Quellcode ggf. ``./helloWorld.fare``
4. `Target` ist ein optionales Argument, falls der generierte Java Quellcode mit compilierter Jar gespeichert werden soll
5. Ergo: ```java -jar FARECompiler.jar helloWorld.fare ./output```