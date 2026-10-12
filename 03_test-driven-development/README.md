# Test Driven Development – FancyApp

Vorgabe: [Unterlagen/test-driven-development/UEBUNGEN.md](https://gitlab.com/ch-tbz-it/Stud/m450/m450/-/blob/main/Unterlagen/test-driven-development/UEBUNGEN.md)
Code: [FancyApp/](FancyApp)

## Aufgabe 1 – Setup

PHPUnit 11 wird pro Projekt über Composer installiert (`composer.json`), die Konfiguration steht in `phpunit.xml`.

```bash
cd FancyApp
composer install
vendor/bin/phpunit
```

Ohne lokales PHP geht es auch mit Docker:

```bash
cd FancyApp
docker run --rm -u $(id -u):$(id -g) -v "$PWD":/app -w /app composer:2 composer install
docker run --rm -u $(id -u):$(id -g) -v "$PWD":/app -w /app composer:2 php vendor/bin/phpunit
```

Einzige Anpassung an `Testcases/Test.php`: Wie im Kommentar der Datei beschrieben, wurde für neuere
PHPUnit-Versionen auf `use PHPUnit\Framework\TestCase;` umgestellt. Ausserdem steht vor dem `require_once`-Pfad
jetzt `__DIR__`, damit die Tests von jedem Ordner aus laufen. Die Testfälle selbst sind unverändert.

## Aufgabe 2 – Methoden implementieren (Red → Green)

Jeder Schritt ist ein eigener Commit (`git log -- 03_test-driven-development`):

| Schritt | Ergebnis |
|---|---|
| RED: Vorgabe unverändert ausgeführt | 3 / 3 Tests rot ([docs/01-red.log](FancyApp/docs/01-red.log)) |
| `shortString` implementiert | 1 / 3 grün |
| `calcAverage` implementiert | 2 / 3 grün |
| `getOpposite` implementiert | 3 / 3 grün, 27 Assertions ([docs/02-green.log](FancyApp/docs/02-green.log)) |

Aus den Tests abgeleitete Anforderungen:

- **`shortString($text, $length, $ending = '...')`**
  - Ist `$ending` länger als `$length`, wird `false` zurückgegeben. Das wird zuerst geprüft, auch bei leerem Text.
  - Ist der Text kurz genug, wird er unverändert zurückgegeben.
  - Sonst wird er auf `$length - len($ending)` Zeichen gekürzt und `$ending` angehängt, sodass das Resultat genau `$length` lang ist.
  - Der Testtext enthält ein `ä`. `strlen` zählt Bytes, deshalb werden `mb_strlen` / `mb_substr` verwendet.
- **`calcAverage($values)`**
  - Ein leeres Array ergibt `0`.
  - Numerische Strings wie `'4.5'` zählen wie Zahlen.
  - Sobald ein Wert nicht numerisch ist (`'bestanden'`), wird `false` zurückgegeben.
- **`getOpposite($value, $delimiter = ',')`**
  - Ein Array wird mit `implode` zu einem String, ein String mit `explode` zu einem Array.
  - Andere Typen (z.B. ein Objekt) ergeben `false`.
  - Damit gilt `getOpposite(getOpposite($x)) === $x`.

Beobachtung: Schon im RED-Lauf war die erste Assertion von `testShortString` erfüllt.
`assertEquals('', null)` vergleicht lose (`==`), also gilt `null` als gleich wie `''`. Mit `assertSame` wäre
das aufgefallen.

## Aufgabe 3 – Auswertung

> Zu ergänzen nach dem Vergleich mit anderen Gruppen (Text-Compare-Tool, z.B. `diff` oder der Vergleich in der IDE).

- Gruppe(n), mit denen verglichen wurde:
- Gleich implementiert?
- Abweichungen im Verhalten (z.B. `strlen` statt `mb_strlen`, Umgang mit leerem Delimiter, Typ des Rückgabewerts):
- Sind die Abweichungen gross oder vernachlässigbar?
- Fazit: Beschreiben die Tests die Anforderungen vollständig? Was lassen sie offen?
