@echo off
java --add-opens=java.base/java.lang=ALL-UNNAMED ^
     --add-opens=java.base/java.util=ALL-UNNAMED ^
     --add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED ^
     --add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED ^
     --add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED ^
     --add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED ^
     --add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED ^
     -jar "./target/compiler-1.0-SNAPSHOT-jar-with-dependencies.jar"
PAUSE