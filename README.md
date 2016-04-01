GroovyFO
===

GroovyFO ist der Versuch das gute aus JFOP und Standard FO des abas-ERP zu kombinieren.

Es können nun ohne zusätzliches kompilieren und redeployen JFOP's in Form von Groovyscripten ausgeführt werden. Außerdem sind Konstrukte wie while, for, if ..., Funktionen sowie Klassen möglich.

Kontakt via Slack:

[![Fragen? Anregungen?](https://powerful-waters-31137.herokuapp.com/badge.svg)](https://powerful-waters-31137.herokuapp.com/)

## Installation
Das Projekt von github herunterladen und entpacken. Den Inhalt src Ordners bei den eigenen JFOP einfügen (abas Tools).

JFOP Server redeployen und wie in der Beispiel Sektion beschrieben testen.


## Verwendung
Aufruf eines GroovyFO aus der Kommandoübersicht
```
<Text>de.finetech.groovy.ScriptExcecuter.java GROOVYSCRIPT_WELCHES_AUSGEFÜHRT_WERDEN_SOLL<zeigen>
```
oder in einem Infosystemen hinterlegen
```
de.finetech.groovy.ScriptExcecuter.java GROOVYSCRIPT_WELCHES_AUSGEFÜHRT_WERDEN_SOLL
```

## Beispiel GroovyFO

Text ow1/GROOVYTEST wie folgt anlegen.
```groovy
/*
* Beispiel GroovyFO holt die ersten 100 Teile aus der DB
* und gibt deren Suchwort aus
* ow1/GROOVYTEST
*/

for(def i=0; i<100 && hole("Teil"); i++){
        // Ausgabe auf Konsole
        println ( h.such )
        //alternativ auch println ( h("such") )
}
```
Zum testen Kommando aufrufen mit
```
<Text>de.finetech.groovy.ScriptExcecuter.java ow1/GROOVYTEST<zeigen>
```

## Funktionsweise

Die Klasse de.finetech.groovy.ScriptExcecuter ist ein JFOP welches als ersten Parameter eine Textdatei (Groovyscript) erwartet. 
Dieses wird dann auf Basis der Script-Klasse de.finetech.groovy.AbasBaseScript initialisiert. 
Diese Klasse kapselt einige Funktionen um so schreibarbeit im eigentlichen Script sparen zukönnen.


### Beispiele:

#### Übersicht
| JFOP            | dt | engl |
| --------------- | ------------- | --------|
| EKS.Hvar(...)   | h("von") oder h.von | |
| EKS.Mvar(...)   | m("von") oder m.von | |
| EKS.hole(...)   | hole(...) <br/> hole(String db, SelectBuilder builder) <br/> hole(String db, String selektion)| select(...) <br/> select(String db, SelectBuilder builder) <br/> select(String db, String selektion)|
| EKS.lade(...)   | lade(...) <br/> lade(int puffer, String db, SelectBuilder builder) <br/> lade(int puffer, String db, String selektion)| load(...) <br/> load(int puffer, String db, SelectBuilder builder) <br/> load(int puffer, String db, String selektion)|
| EKS.formel(...) | fo(String variable, wert)| |
| EKS.getValue(puffer, varName)| l1("von") oder l1.von <br/> l2("von") oder l2.von <br/> usw...| |
| EKS.println(...)| println(...)| |
| EKS.box(...,...)| box(...,...)| |
| EKS.eingabe(...)| ein(...)| in(...)|
| EKS.bringe(...) | bringe(...)| rewrite(...)|
| EKS.mache(...) | mache(...)| make()|
| EKS.mache("maske zeile +O") | plusZeile()| addRow()|
| EKS.bringe("maske zeile -O") | entfZeile()| removeRow()|
| EKS.Dvar(...) | dazu("von") oder d.von| add("von") or d.von|
| | mehr() | success() or more()|
|... | ... | ... |

### Helferlein

#### SelectionBuilder
Der SelectionBuilder ist eine Hilfsklasse um einfach Selektion definieren zu können.
```groovy
// Artikel von a bis b
def selection1 = new SelectionBuilder().normal("such2","A","B").database(2).group(1)
// im Matchcode auf den Namen in Bediensprache
def selection2 = new SelectionBuilder().matchcode("namebspr","Schif*fahrt")
...
```

#### Infosystemcall
Die Klasse Infosystemcall ruft mittels edpinfosys.sh Infosystem auf und stellt das Ergebnis des Aufrufes
in Variablen bereit
```groovy
def result = Infosystemcall.build("VKZENTRALE")
        .addTableOutputField("ttrans")
        .addTableOutputField("tdate")
        .addTableOutputField("tkuli")
        .addTableOutputField("tklname")
        .addTableOutputField("teprice")
        .setHeadParameter("vktyp", "Rechnung")
        .setHeadParameter("ablageart", "abgelegt")
        .setHeadParameter("ynurger", "ja")
        .setHeadParameter("datef", "-30")
        .setHeadParameter("datet", ".")
        .setHeadParameter("bstart", "1")
        .execute()
        
for(def row: result.table){
        println row.ttrans
        println row.tdate
        println row.tkuli
        println row.tklname
        println row.teprice
}
```


Die Implementierung ist bei weitem nicht Vollständig!
