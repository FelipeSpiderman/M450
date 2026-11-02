# Video-Skript TDD (FancyApp)

Dauer: etwa 6 bis 8 Minuten. Pro Abschnitt steht unter **Zeigen**, was auf dem Bildschirm sein soll, und unter
**Sagen**, was ich vorlese. Text in eckigen Klammern ist nur ein Hinweis für mich und wird nicht vorgelesen.

## Vorbereitung (vor der Aufnahme)

1. VS Code im Ordner M450 öffnen.
2. Ein Terminal im Ordner 03_test-driven-development/FancyApp öffnen.
3. Die Tests einmal laufen lassen und prüfen, dass „OK (3 tests, 27 assertions)“ kommt. Danach das Terminal leeren.
4. Diese Tabs offen haben: README.md, Testcases/Test.php, Application/MyFancyClass.php, docs/01-red.log,
   docs/vergleich/MyFancyClass_GruppeB.php.
5. Schriftgrösse in VS Code und im Terminal gross genug stellen, damit man es im Video lesen kann.

---

## 1. Einleitung (ca. 30 Sekunden)

**Zeigen:** README.md (oberer Teil)

**Sagen:**
„Hallo, in diesem Video zeige ich die Übung Test Driven Development aus dem Modul 450. Die Idee von TDD ist, dass
zuerst die Tests geschrieben werden. Die Tests beschreiben, was das Programm können muss. Danach wird der Code so
lange geschrieben, bis alle Tests grün sind. In dieser Übung waren die Tests schon vorgegeben, und ich musste die
drei Methoden der Klasse MyFancyClass so umsetzen, dass alle Tests bestehen.“

## 2. Aufgabe 1: Setup (ca. 1 Minute)

**Zeigen:** composer.json und phpunit.xml kurz öffnen, danach Testcases/Test.php oben (die Zeilen mit
`require_once` und `extends TestCase`)

**Sagen:**
„Für das Setup habe ich PHPUnit 11 über Composer installiert. In der composer.json steht PHPUnit als Abhängigkeit,
und in der phpunit.xml ist festgelegt, welche Testdatei ausgeführt wird. Ich habe kein PHP lokal installiert,
deshalb starte ich alles über Docker mit dem Image composer:2.

Die Vorgabe war für eine alte PHPUnit-Version geschrieben. Deshalb musste ich zwei kleine Sachen in der Testdatei
anpassen: Die Klasse erbt jetzt von TestCase statt von PHPUnit_Framework_TestCase, das steht so auch im Kommentar
der Datei. Und beim require_once habe ich __DIR__ ergänzt, damit der Pfad immer stimmt. Die Testfälle selbst habe
ich nicht verändert.“

## 3. Der rote Zustand (ca. 1 Minute)

**Zeigen:** docs/01-red.log

**Sagen:**
„Bevor ich etwas implementiert habe, habe ich die Tests einmal laufen lassen. Diese Ausgabe habe ich hier
gespeichert. Man sieht drei F, also drei fehlgeschlagene Tests. Die Methoden waren leer und haben null
zurückgegeben, deshalb steht überall ‚Failed asserting that null matches expected‘.

Interessant ist: Bei testShortString ist die erste Assertion trotzdem durchgegangen. Dort wird ein leerer String
mit null verglichen, und assertEquals vergleicht nur lose. Für assertEquals ist null gleich wie ein leerer String.
Mit assertSame wäre dieser Test auch rot gewesen.“

## 4. Aufgabe 2: Die Tests lesen (ca. 1 Minute)

**Zeigen:** Testcases/Test.php, langsam durch die drei Testmethoden scrollen

**Sagen:**
„Hier sind die drei Testfälle. Jede Methode, die mit test anfängt, ist ein Testfall, und jede assert-Zeile ist eine
Bedingung, die erfüllt sein muss.

testShortString prüft, dass ein Text auf eine bestimmte Länge gekürzt wird und am Ende drei Punkte oder ein anderes
Ende bekommt. testCalcAverage prüft den Durchschnitt einer Liste, auch wenn die Zahlen als Text kommen. Und
testGetOpposite prüft, dass aus einem Array ein String wird und aus einem String ein Array.“

## 5. Aufgabe 2: Die Umsetzung (ca. 2 Minuten)

**Zeigen:** Application/MyFancyClass.php, bei jeder Methode kurz stehen bleiben

**Sagen (shortString):**
„Bei shortString prüfe ich zuerst, ob das Ende überhaupt in die erlaubte Länge passt. Wenn nicht, gibt die Methode
false zurück, das verlangt der Test so. Wenn der Text schon kurz genug ist, kommt er unverändert zurück. Sonst
schneide ich ihn ab und hänge das Ende an. Ich verwende die mb-Funktionen, weil im Testtext ein ä vorkommt. strlen
zählt Bytes, und ein ä hat zwei Bytes. mb_strlen zählt Zeichen.“

**Sagen (calcAverage):**
„Bei calcAverage gibt eine leere Liste 0 zurück. Dann gehe ich jeden Wert durch. Mit is_numeric prüfe ich, ob es
eine Zahl ist. Zahlen als Text wie ‚4.5‘ sind erlaubt, Wörter wie ‚bestanden‘ nicht, dann kommt false zurück. Am
Schluss teile ich die Summe durch die Anzahl.“

**Sagen (getOpposite):**
„getOpposite dreht den Typ um. Ein Array wird mit implode zu einem String, ein String wird mit explode zu einem
Array. Wenn man die Methode zweimal aufruft, kommt wieder das Original heraus, das prüfen mehrere Tests. Für alles
andere, zum Beispiel ein Objekt, kommt false zurück. Ein leeres Trennzeichen fange ich auch ab, weil explode sonst
einen Fehler wirft.“

## 6. Der grüne Zustand (ca. 1 Minute)

**Zeigen:** Terminal, Tests live ausführen [Befehl siehe unten bei „Befehle“]. Danach in VS Code die
Git-Historie der Datei MyFancyClass.php zeigen (Timeline unten links im Explorer).

**Sagen:**
„Jetzt lasse ich die Tests laufen. … Alle drei Tests sind grün, mit 27 Assertions.

In der Git-Historie sieht man, dass ich Schritt für Schritt vorgegangen bin: zuerst der rote Zustand mit allen drei
Tests rot, dann ein Commit pro Methode, bis am Schluss alle drei grün waren. So ist der TDD-Kreislauf gedacht:
rot, grün, aufräumen.“

## 7. Aufgabe 3: Vergleich (ca. 1,5 Minuten)

**Zeigen:** docs/vergleich/MyFancyClass_GruppeB.php und MyFancyClass.php nebeneinander (beide markieren,
Rechtsklick, „Compare Selected“), danach die zwei Tabellen in der README unter Aufgabe 3

**Sagen:**
„In Aufgabe 3 soll man die eigene Lösung mit einer anderen Gruppe vergleichen. Ich hatte keinen Partner, deshalb
habe ich den Vergleich simuliert. Gruppe B ist eine zweite Lösung, die anders gebaut ist, aber dieselben 27
Assertions besteht.

Die Unterschiede: Gruppe B kürzt den Text mit mb_strimwidth, rechnet mit array_sum und prüft das leere Trennzeichen
nicht.

Bei allem, was die Tests prüfen, machen beide Lösungen genau dasselbe. Bei Eingaben ohne Test gibt es aber
Unterschiede. Bei japanischem Text kürzt Gruppe B stärker, weil mb_strimwidth die Breite auf dem Bildschirm zählt
und nicht die Anzahl Zeichen. Wenn man null als Liste übergibt, stürzt meine Lösung ab und Gruppe B gibt 0 zurück.
Bei einem leeren Trennzeichen ist es umgekehrt, da stürzt Gruppe B ab.

Für die Übung sind das kleine Unterschiede, weil beide Lösungen alle Anforderungen erfüllen. In einem echten
Programm wären zwei davon aber Abstürze.“

## 8. Fazit (ca. 30 Sekunden)

**Zeigen:** README.md, Abschnitt Erkenntnis und KI-Nutzung

**Sagen:**
„Was ich aus der Übung mitnehme: Bei TDD sind die Tests die Anforderungen, aber nur die Anforderungen, die jemand
aufgeschrieben hat. Was nicht getestet ist, kann jede Gruppe anders lösen. Wenn ein Verhalten wichtig ist, braucht
es einen Test dafür. Und man sollte überlegen, ob assertSame besser passt als assertEquals.

Zum Schluss: Ich habe für diese Übung Claude Code als KI-Werkzeug verwendet. Das ist in der README unter KI-Nutzung
deklariert. Danke fürs Zuschauen.“
