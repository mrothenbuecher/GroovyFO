Status:**alpha**

GroovyFO
===

GroovyFO ist der Versuch das gute aus JFOP und Standard FO des abas-ERP zu kombinieren.

GroovyFO vereinfacht den Umgang mit JFOP durch das bereitstellen eines einfachen an FOP angelehnten Syntaxes.

Es können nun ohne zusätzliches kompilieren und redeployen JFOP's in Form von Groovyscripten ausgeführt werden. Außerdem sind Konstrukte wie while, for, if ..., Funktionen sowie Klassen möglich.

einige Vorteile:<br>
* [automatische Typkonvertierung](https://github.com/mkuerbis/GroovyFO/wiki/automatische-Typkonvertierung),beim Zugriff auf abas Variablen (bsp.: I3 -> Integer)
* Syntax an Standard FO angelehnt
    * Wertzuweisung `M|datum = M|datum +1`
* Zugriff mittels Dachoperator `H|platz^id`
* Kontrollstrukturen aus Java
    * `for`, `while`, `if`, `switch`, Methoden, Klassen ...
* Syntax vereinfachung gegenüber JFOP
* kein Redeployen nach Änderungen an GroovyFO-Scripten nötig, änderungen an den Scripten sind sofort wirksam
* diverse Helferklassen für Selektion, Infosystemaufrufe, Charts usw.
* Ablaufsteuerung durch die Methoden `always` und `onerror`

Kontakt via Slack:

[![Fragen? Anregungen?](https://powerful-waters-31137.herokuapp.com/badge.svg)](https://powerful-waters-31137.herokuapp.com/)

[Übersicht zur API](https://github.com/mkuerbis/GroovyFO/wiki/API)

[Benchmark](https://github.com/mkuerbis/GroovyFO/wiki/Benchmark)

## Funktionsweise

Die Klasse de.finetech.groovy.ScriptExcecuter ist ein JFOP welches als ersten Parameter eine Textdatei (Groovyscript) erwartet. Dieses wird dann auf Basis der Script-Klasse de.finetech.groovy.AbasBaseScript initialisiert. Diese Klasse kapselt JFOP-Funktionen um so Schreibarbeit sparen zu können. Weiterhin besteht durch die Verwendung von Groovy die Möglichkeit Kontrollstrukturen wie if-Anweisungen, Schleifen oder eben auch Klassen im Kontext eines FOP zu verwenden. Dadurch das bei jeden Aufruf des ScriptExecuters die Groovy Datei neu interpretiert wird, können Änderungen an dieser Datei ohne erneutes redeployen des JFOP-Server wirksam werden. Dieses Verhalten ist ähnlich dem Verhalten von FOP's.


## Installation

### via Quelltext
Das Projekt von github herunterladen und entpacken. Den Inhalt src Ordners bei den eigenen JFOP einfügen (abas Tools).

JFOP Server redeployen und wie in der Beispiel Sektion beschrieben testen.

### via Release
*.jar Datei des gewünschten Releases herunterladen und nach `~s3/java/lib` kopieren. Anschließend `~s3/java/thirdparty_homedir.classpath` ergänzen und JFOP Server neustarten.


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

Text ow1/GROOVYFO.TEST wie folgt anlegen.
```groovy
/*
* Beispiel GroovyFO holt die ersten 100 Teile aus der DB
* und gibt deren Suchwort aus
* ow1/GROOVYFO.TEST
*/

for(def i=0; i<100 && hole("Teil"); i++){
        // Ausgabe auf Konsole
        println ( h.such )
}
```
Zum testen Kommando aufrufen mit
```
<Text>de.finetech.groovy.ScriptExcecuter.java ow1/GROOVYFO.TEST<zeigen>
```

#### Kurzübersicht
| JFOP            | dt | engl |
| --------------- | ------------- | --------|
| EKS.Hvar(...)   | h.von oder h("von") oder h\|von| |
| EKS.Mvar(...)   | m.von oder m("von") oder m\|von| |
| EKS.hole(...)   | hole(...) <br/> hole(String db, SelectBuilder builder) <br/> hole(String db, String selektion)| select(...) <br/> select(String db, SelectBuilder builder) <br/> select(String db, String selektion)|
| EKS.lade(...)   | lade(...) <br/> lade(int puffer, String db, SelectBuilder builder) <br/> lade(int puffer, String db, String selektion)| load(...) <br/> load(int puffer, String db, SelectBuilder builder) <br/> load(int puffer, String db, String selektion)|
| EKS.formel(...) | fo(String variable, wert)| |
| EKS.getValue(puffer, varName)| l1("von") oder l1.von ... <br/> l2("von") oder l2.von ... <br/> usw...| |
| EKS.println(...)| println(...)| |
| EKS.box(...,...)| box(...,...)| |
| EKS.eingabe(...)| ein(...)| in(...)|
| EKS.bringe(...) | bringe(...)| rewrite(...)|
| EKS.mache(...) | mache(...)| make()|
| EKS.mache("maske zeile +O") | plusZeile()| addRow()|
| EKS.bringe("maske zeile -O") | entfZeile()| removeRow()|
| EKS.Dvar(...) | d.von| add("von") oder d.von ...|
| | mehr() | success() or more()|
|... | ... | ... |

### Ablaufsteuerung

In jedem GroovyFO gibt es die Möglichkeit die Methoden `onerror(Exception ex)` und `always()` zu überschreiben.

`onerror` wird immer dann ausgeführt, solange sich das GroovyFO übersetzen lässt und wenn eine unbehandelte Ausnahme im Code des GroovyFO auftritt.

`always` wird, solange sich das GroovyFO übersetzen lässt, immer am Ende ausgeführt. <br>

```groovy

def onerror(def ex){
        // was ist zutun wenn ein Fehler beim ausführen des GroovyFO auftritt?
}

def always(){
        // am ende des GroovyFO, was soll immer gemacht werden?
        // diese Methode wird auch nach einem Fehler ausgeführt
}

```

### Helferlein

#### SelectionBuilder
Der [SelectionBuilder](https://github.com/mkuerbis/GroovyFO/wiki/SelectionBuilder) ist eine Hilfsklasse um einfach Selektion definieren zu können.
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
#### ChartGenerator

Eine Klasse zum erzeugen von Charts
```groovy
// Liniendiagramm erzeugen
ChartGenerator gen = ChartGenerator.create(ChartType.LINES);
// Titel setzen
gen.setChartTitle("Hello World");
gen.setMarkerx("first", "second", "third", "fourth", "fifth")
gen.setAnglex(45)


// neue Datenserie definieren
DataSeries series = new DataSeries();
series.setTitle("Series 1");
series.addValue(Value.create(1));
series.addValue(Value.create(2));
series.addValue(Value.create(3));
series.addValue(Value.create(4));
series.addValue(Value.create(5));

// weiter Serie
DataSeries series2 = new DataSeries();
series2.setTitle("Series 2");
series2.addValue(Value.create(5));
series2.addValue(Value.create(4));
series2.addValue(Value.create(3));
series2.addValue(Value.create(2));
series2.addValue(Value.create(1));

gen.setOptions(true)

// neue Datenserie dem Chart bekannt machen
gen.addDataseries(series);
gen.addDataseries(series2);

// eigentliche generien des Charts im abas
// ychart ist im Kopfbereich ein Chartfeld
gen.generate("ychart");
```
Ergebnis:

![Chart](https://raw.githubusercontent.com/mkuerbis/GroovyFO/master/img/chart.jpg)
