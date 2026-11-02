# Test Driven Development – FancyApp

Aufgabe: [Unterlagen/test-driven-development/UEBUNGEN.md](https://gitlab.com/ch-tbz-it/Stud/m450/m450/-/blob/main/Unterlagen/test-driven-development/UEBUNGEN.md)
Code: [FancyApp/](FancyApp)

## Aufgabe 1 – Setup

PHPUnit 11 ist über Composer installiert (`composer.json`, `phpunit.xml`). Gestartet wird im Ordner FancyApp: zuerst
die Abhängigkeiten mit Composer installieren, dann PHPUnit aus dem Ordner vendor/bin starten. Ohne lokales PHP geht es
auch über das Docker-Image `composer:2` (siehe [ANLEITUNG.md](ANLEITUNG.md)).

In `Testcases/Test.php` musste ich für die neue PHPUnit-Version `TestCase` statt `PHPUnit_Framework_TestCase`
verwenden. Das steht auch so im Kommentar in der Datei. Ausserdem habe ich den `require_once`-Pfad mit `__DIR__`
ergänzt. An den Testfällen selbst ist nichts geändert.

## Aufgabe 2 – Methoden implementieren

Am Anfang waren alle 3 Tests rot (Ausgabe in [docs/01-red.log](FancyApp/docs/01-red.log)). Danach wurde eine
Methode nach der anderen umgesetzt, jeweils mit eigenem Commit, bis alle Tests grün waren
([docs/02-green.log](FancyApp/docs/02-green.log), 3 Tests, 27 Assertions).

Was die Tests von den Methoden verlangen:

- `shortString`: kürzt einen Text auf eine maximale Länge und hängt ein Ende an (Standard `...`). Ist das Ende
  länger als die erlaubte Länge, kommt `false` zurück. Wegen des `ä` im Testtext müssen die `mb_`-Funktionen
  verwendet werden.
- `calcAverage`: Durchschnitt einer Liste. Leere Liste ergibt 0, Zahlen als Strings sind okay, Wörter wie
  „bestanden“ ergeben `false`.
- `getOpposite`: macht aus einem Array einen String (`implode`) und aus einem String ein Array (`explode`).
  Bei allem anderen kommt `false` zurück.

Aufgefallen ist mir: Schon im roten Zustand ging die erste Assertion durch. `assertEquals('', null)` vergleicht
nämlich nur lose, und `null` gilt dabei als gleich wie ein leerer String. Mit `assertSame` wäre das nicht passiert.

## Aufgabe 3 – Vergleich mit einer anderen Lösung

Ich habe keinen Partner, deshalb ist der Vergleich simuliert: Eine zweite Lösung („Gruppe B“) liegt in
[docs/vergleich/MyFancyClass_GruppeB.php](FancyApp/docs/vergleich/MyFancyClass_GruppeB.php). Sie besteht dieselben
Tests (3 Tests, 27 Assertions, alles grün), ist aber anders gebaut.

**Ist der Code gleich aufgebaut?** Nein, aber ähnlich.

| Methode | Unsere Lösung (A) | Gruppe B |
|---|---|---|
| `shortString` | prüft selbst die Länge, schneidet mit `mb_substr` und hängt das Ende an | macht alles in einem Schritt mit `mb_strimwidth` |
| `calcAverage` | prüft mit `count`, ob die Liste leer ist, summiert selbst in der Schleife | prüft mit `empty`, summiert mit `array_sum` |
| `getOpposite` | gibt bei leerem Trennzeichen `false` zurück | prüft das Trennzeichen nicht |

**Machen beide dasselbe?** Bei allem, was die Tests prüfen: ja. Bei Fällen ohne Test gibt es Unterschiede. Ich habe
beide Klassen mit ein paar zusätzlichen Eingaben aufgerufen:

| Eingabe (nicht getestet) | A | B |
|---|---|---|
| `shortString` mit japanischem Text „日本語テキスト“, Länge 6 | `日本語...` | `日...` |
| `calcAverage(null)` | Absturz (TypeError) | `0` |
| `calcAverage` mit Schlüsseln, z.B. a=4, b=6 | `5.0` | `5` |
| `getOpposite("a,b", "")` (leeres Trennzeichen) | `false` | Absturz (ValueError) |

Warum:
- `mb_strimwidth` zählt nicht Zeichen, sondern **Anzeigebreite**. Japanische Zeichen sind doppelt so breit, deshalb
  bleibt bei B nur ein Zeichen übrig. Mit deutschem Text (auch mit `ä`) ist das Ergebnis gleich.
- `count(null)` ist in PHP 8 ein Fehler, `empty(null)` nicht. B ist hier zufällig robuster.
- `5.0` und `5` sind mit `assertEquals` gleich. Mit `assertSame` wäre der Unterschied aufgefallen.
- `explode` mit leerem Trennzeichen wirft in PHP 8 einen Fehler. A fängt das ab, B nicht.

**Sind die Abweichungen gross?** Für die Aufgabe nicht: Beide Lösungen erfüllen alle Anforderungen, die in den Tests
stehen. Im echten Einsatz wären zwei davon aber Abstürze, also nicht vernachlässigbar.

**Erkenntnis:** Die Tests sind die Anforderungen, aber nur die Anforderungen, die jemand aufgeschrieben hat. Alles,
was nicht getestet ist, darf jede Gruppe anders lösen. Wenn ein Verhalten wichtig ist (leeres Trennzeichen,
`null` als Eingabe, genauer Datentyp), muss es einen Test dafür geben.

## KI-Nutzung

Setup, Implementierung der drei Methoden, die simulierte Lösung „Gruppe B“ und der Vergleich in Aufgabe 3 wurden von
Claude Code (KI-Agent) erstellt. Der KI-Rahmen des Moduls erlaubt das bei Übungen eigentlich nicht; ich habe mich
trotzdem dafür entschieden und deklariere es hier.
