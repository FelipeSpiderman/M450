# Anleitung TDD (für mich)

## Stand der Übung

- Aufgabe 1 (Setup): erledigt. PHPUnit 11 läuft über Composer.
- Aufgabe 2 (Methoden): erledigt. Alle 3 Tests sind grün (27 Assertions).
- Aufgabe 3 (Vergleich): erledigt, aber simuliert mit einer zweiten Lösung „Gruppe B“, weil ich keinen Partner habe.
  Details stehen in der [README](README.md).

## Tests laufen lassen

PHP muss nicht installiert sein, Docker reicht. Ich gehe in den Ordner FancyApp und starte das Docker-Image
`composer:2` mit dem aktuellen Ordner als Arbeitsordner. Darin führe ich zuerst einmal „composer install“ aus (lädt
PHPUnit in den Ordner vendor) und danach PHPUnit aus vendor/bin. Am Ende sollte „OK (3 tests, 27 assertions)“
stehen.

Mit lokal installiertem PHP (Pakete php-cli, php-xml, php-mbstring und composer) geht es gleich, nur ohne Docker.

## Was am Setup angepasst wurde

Die Vorgabe ist für eine alte PHPUnit-Version geschrieben. Zwei kleine Änderungen in Testcases/Test.php:
- Die Testklasse erbt von `TestCase` statt von `PHPUnit_Framework_TestCase`. Das steht so auch im Kommentar der Datei.
- Der Pfad beim `require_once` beginnt mit `__DIR__`, damit er stimmt, egal von wo man die Tests startet.

Die Testfälle selbst sind unverändert.

## Wie die drei Methoden funktionieren

**shortString** kürzt einen Text:
1. Ist das Ende (z.B. „...“) länger als die erlaubte Länge, gibt es `false`, weil das Ende gar nicht hineinpasst.
2. Ist der Text kurz genug, kommt er unverändert zurück.
3. Sonst wird der Text so weit abgeschnitten, dass Text plus Ende genau die erlaubte Länge haben.

Es werden die `mb_`-Funktionen verwendet, weil der Testtext ein `ä` enthält. Normales `strlen` zählt Bytes (ä = 2),
`mb_strlen` zählt Zeichen (ä = 1).

**calcAverage** rechnet den Durchschnitt:
1. Leere Liste ergibt 0.
2. Ist ein Wert keine Zahl (z.B. „bestanden“), gibt es `false`. Zahlen als Text wie „4.5“ sind erlaubt
   (`is_numeric` erkennt beides).
3. Sonst Summe geteilt durch Anzahl.

**getOpposite** dreht den Typ um:
- Array wird zu String: Werte mit dem Trennzeichen zusammenkleben (`implode`).
- String wird zu Array: beim Trennzeichen aufteilen (`explode`).
- Alles andere (z.B. ein Objekt) und ein leeres Trennzeichen ergeben `false`.

Zweimal hintereinander aufgerufen kommt also wieder das Original heraus. Genau das prüfen mehrere Tests.

## Der TDD-Kreislauf (so war der Ablauf)

1. **Red:** Tests laufen lassen und schauen, welche Assertion als erste fehlschlägt.
2. **Green:** nur so viel Code schreiben, dass diese Assertion durchgeht.
3. Tests wieder laufen lassen und weiter zur nächsten Assertion.
4. **Refactor:** wenn alles grün ist, den Code aufräumen. Die Tests müssen grün bleiben.
5. Nach jeder fertigen Methode committen.

Die Ausgaben vom roten und vom grünen Zustand liegen in FancyApp/docs (01-red.log und 02-green.log).

## Aufgabe 3: Was ich daraus lernen soll

Die zweite Lösung in FancyApp/docs/vergleich besteht dieselben Tests, verhält sich aber bei ungetesteten Eingaben
anders (japanischer Text, `null` als Liste, leeres Trennzeichen). Die Kernaussage für eine Prüfung oder Präsentation:

- Bei TDD sind die Tests die Anforderungen.
- Was nicht getestet ist, ist nicht festgelegt. Zwei korrekte Lösungen können sich dort unterscheiden.
- `assertEquals` vergleicht lose (5 und 5.0 sind gleich, '' und null auch), `assertSame` vergleicht streng.

Wenn ich doch noch mit einer echten Gruppe vergleiche: In VS Code beide Dateien markieren, Rechtsklick,
„Compare Selected“. Die Tabelle in der README dann mit deren Lösung ergänzen.
