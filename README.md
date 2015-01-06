GroovyFO
===

GroovyFO ist der Versuch das gute aus JFOP und Standard FO des abas-ERP zu kombinieren.

Es können nun ohne zusätzliches kompilieren JFOP's in Form von GroovyScripts ausgeführt werden. Außerdem sind
nun while, for, if ..., Funktionen sowie Klassen möglich.

Installation
===
Die GroovyFO.jar aus dem Release Verzeichnis in das homedir_java\java\lib kopieren.

JFOP Server redeployen und wie in der Bespiel Sektion beschrieben testen.


Verwendung
===
```
<Text>de.finetech.groovy.ScriptExcecuter.java GROOVYSCRIPT_WELCHES_AUSGEFÜHRT_WERDEN_SOLL<zeigen>
```

Beispiel GroovyFO
===

Text ow1/GROOVYTEST wie folgt anlegen.
```groovy
/*
* Beispiel GroovyFO holt die ersten 100 Teile aus der DB
* ow1/GROOVYTEST
*/

for(i in 0..99){
        // wenn keine Teile mehr geholte werden können abbruch
        if(!hole("Teil"))
                break
        // Ausgabe auf Konsole
        println ( h("such") )
}
```
Zum testen Kommando aufrufen mit
```
<Text>de.finetech.groovy.ScriptExcecuter.java ow1/GROOVYTEST<zeigen>
```
