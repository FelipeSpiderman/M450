# Test Driven Development – FancyApp

Aufgabe: [Unterlagen/test-driven-development/UEBUNGEN.md](https://gitlab.com/ch-tbz-it/Stud/m450/m450/-/blob/main/Unterlagen/test-driven-development/UEBUNGEN.md)
Code: [FancyApp/](FancyApp)

## Aufgabe 1 – Setup

PHPUnit 11 ist über Composer installiert (`composer.json`, `phpunit.xml`). Gestartet wird mit:

```bash
cd FancyApp
composer install
vendor/bin/phpunit
```

Ohne lokales PHP geht es auch über das Docker-Image `composer:2`.

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

## Aufgabe 3 – Vergleich mit anderen Gruppen

*Noch offen.*

## KI-Nutzung

Das Setup und die Implementierung der drei Methoden hat Claude Code (KI-Agent) erstellt. Der KI-Rahmen des Moduls
erlaubt das nicht. Deshalb löse ich die Methoden noch selbst und passe diesen Abschnitt danach an.
