GroovyFO
===

GroovyFO ist der Versuch das gute aus JFOP und Standard FO des abas-ERP zu kombinieren.

Es können nun ohne zusätzliches kompilieren JFOP's in Form von Groovyscripten ausgeführt werden. Außerdem sind Konstrukte
nun while, for, if ..., Funktionen sowie Klassen möglich.

## Installation
Die [GroovyFO.zip](release/GroovyFO.zip) aus dem release Ordner herunterladen und öffnen. Den enthaltenen de - Ordner in das Verzeichnis manddir_java\java\classes kopieren.

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

for(def i=0; i<100 && hole("Teil"); i++){
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


### Beispiele:

#### SelectBuilder
Der SelectBuilder ist eine Hilfsklasse um einfach Selektion definieren zu können.
```groovy
// Artikel von a bis b
def selection1 = new SelectionBuilder().normal("such2","A","B").database(2).group(1)
// im Matchcode auf den Namen in Bediensprache
def selection2 = new SelectionBuilder().matchcode("namebspr","Schif*fahrt")
...
```


#### Übersicht
| JFOP            | GroovyFO |
| --------------- | ------------- |
| EKS.Hvar(...)   | h(...) |
| EKS.Mvar(...)   | m(...) |
| EKS.hole(...)   | hole(...) <br/> hole(String db, SelectBuilder builder) <br/> hole(String db, String selektion)|
| EKS.lade(...)   | lade(...) <br/> lade(int puffer, String db, SelectBuilder builder) <br/> lade(int puffer, String db, String selektion)|
| EKS.formel(...) | fo(String variable, wert)|
| EKS.getValue(puffer, varName)| l1(...) <br> l2(...)|
| EKS.println(...)| println(...)|

Die Implementierung ist bei weitem nicht Vollständig!
