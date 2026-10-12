# Anleitung TDD (für mich)

## Tests laufen lassen

PHP muss nicht installiert sein, Docker reicht:

```bash
cd 03_test-driven-development/FancyApp
docker run --rm -u $(id -u):$(id -g) -v "$PWD":/app -w /app composer:2 composer install
docker run --rm -u $(id -u):$(id -g) -v "$PWD":/app -w /app composer:2 php vendor/bin/phpunit
```

Mit lokalem PHP (`sudo apt install php-cli php-xml php-mbstring composer`):

```bash
composer install
vendor/bin/phpunit
```

## Was am Setup angepasst wurde

Die Vorgabe ist für eine alte PHPUnit-Version geschrieben. Zwei kleine Änderungen in `Testcases/Test.php`:
- `class Test extends TestCase` statt `PHPUnit_Framework_TestCase`. Das steht so auch im Kommentar der Datei.
- `require_once(__DIR__ . '/../Application/MyFancyClass.php')`, damit der Pfad stimmt, egal von wo man startet.

Die Testfälle selbst sind unverändert.

## Die Übung selbst lösen

Laut KI-Rahmen des Moduls soll ich die Übung selbst lösen. Die Methoden in `MyFancyClass.php` sind im Moment von
Claude geschrieben. So setze ich auf den roten Startzustand zurück:

```bash
git checkout 4add1f2 -- 03_test-driven-development/FancyApp/Application/MyFancyClass.php
```

Dann sind alle drei Methoden wieder leer und alle Tests rot. Danach immer im Kreis:

1. **Red:** Tests laufen lassen und schauen, welche Assertion als erste fehlschlägt
2. **Green:** nur so viel Code schreiben, dass diese Assertion durchgeht
3. Tests wieder laufen lassen und weiter zur nächsten Assertion
4. **Refactor:** wenn alles grün ist, den Code aufräumen, die Tests müssen grün bleiben
5. Nach jeder fertigen Methode committen

Tipps, wenn ich steckenbleibe:
- Der Testtext enthält ein `ä`. `strlen('ä')` ist 2 (Bytes), `mb_strlen('ä')` ist 1 (Zeichen).
- `is_numeric('4.5')` ist `true`, `is_numeric('bestanden')` ist `false`.
- `implode` macht aus einem Array einen String, `explode` macht aus einem String ein Array.
- Bei `shortString`: Was passiert, wenn das `$ending` länger ist als `$length`? Die Tests sagen es.

## Aufgabe 3: Vergleich

Mit einer anderen Gruppe den Code vergleichen, z.B. mit `diff` oder dem Vergleich in VS Code (zwei Dateien
markieren → Rechtsklick → „Compare Selected“). Notieren:
- Sind die Lösungen gleich aufgebaut?
- Machen sie dasselbe, auch bei Fällen, die nicht getestet sind (z.B. leerer Delimiter)?
- Was sagen die Tests **nicht**? Das ist die eigentliche Erkenntnis der Übung.

Das kommt dann in die README unter „Aufgabe 3“.
