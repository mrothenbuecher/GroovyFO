GroovyFO
===

GroovyFO ist der Versuch das gute aus JFOP und Standard FO des abas-ERP zu kombinieren.

Es können nun ohne zusätzliches kompilieren JFOP's in Form von Groovyscripten ausgeführt werden. Außerdem sind Konstrukte
nun while, for, if ..., Funktionen sowie Klassen möglich.

## Installation
Die GroovyFO.zip im release Ordner herunterladen und öffnen. Denn de - Ordner in das Verzeichnis manddir_java\java\classes kopieren.

JFOP Server redeployen und wie in der Bespiel Sektion beschrieben testen.


## Verwendung
```
<Text>de.finetech.groovy.ScriptExcecuter.java GROOVYSCRIPT_WELCHES_AUSGEFÜHRT_WERDEN_SOLL<zeigen>
```

## Beispiel GroovyFO

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

## Funktionsweise

Die Klasse de.finetech.groovy.ScriptExcecuter ist ein JFOP welches als ersten Parameter eine Textdatei (Groovyscript) erwartet. 
Dieses wird dann auf Basis der Script-Klasse de.finetech.groovy.AbasBaseScript initialisiert. 
Diese Script-Klasse kapselt einige Funktionen um so schreibarbeit im eigentlichen Script sparen zukönnen.

Beispiele:

| JFOP            | GroovyFO |
| --------------- | ------------- |
| EKS.Hvar(...)   | h(...) |
| EKS.hole(...)   | hole(...) |
