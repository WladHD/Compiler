## Steps
1. Dependencies von pom.xml installieren (automatisch mit IntelliJ)
2. JavaCC IntelliJ Plugin installieren https://plugins.jetbrains.com/plugin/11431-javacc

## JavaCC Grammatik Kompilieren
1. Grammatik \*.jjt unter de.fh.javacc speichern
2. ``mvn generate-sources`` ausführen
3. Generierte Grammatik wird unter de.fh.javacc.generated(.ast) gespeichert

## Referenzen
https://cs.lmu.edu/~ray/notes/javacc/
https://www.mojohaus.org/javacc-maven-plugin/
https://stackoverflow.com/a/13921386