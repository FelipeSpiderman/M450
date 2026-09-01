# Grundlagen

## Aufgabe 1

### Welche Formen von Tests kennen wir aus der Informatik?

Es gibt verschiedene Arten von Tests, je nachdem, auf welcher Ebene man testet und was man testen möchte. Hier drei Beispiele, die uns aus der Praxis bekannt sind:

**1. Unit-Tests (Komponententests)**

Unit-Tests prüfen einzelne Funktionen oder Methoden – und zwar komplett isoliert. Man gibt bestimmte Werte rein und schaut, ob das herauskommt, was man erwartet. In der Praxis schreibt man dafür automatisierte Tests, zum Beispiel mit JUnit oder pytest, die dann bei jedem Build mitlaufen.

*Beispiel:* Eine Methode `BerechnePreis()` wird mit verschiedenen Kombinationen von Grundpreis und Rabatt aufgerufen, damit man sicher sein kann, dass die Berechnung stimmt.

**2. Integrationstests**

Bei Integrationstests schaut man, ob mehrere Komponenten zusammen richtig funktionieren. Das heisst, man testet nicht mehr nur eine einzelne Funktion, sondern das Zusammenspiel zwischen Modulen – etwa ob die Datenbankanbindung mit der Business Logic zusammenarbeitet und Daten korrekt übertragen werden.

*Beispiel:* Man testet, ob eine Bestellung, die über die App erfasst wird, auch wirklich korrekt in der Datenbank landt und die Bestellbestätigung stimmt.

**3. System- und Abnahmetests**

Hier wird das komplette System als Ganzes getestet – also so, wie der Endkunde es benutzen würde. Oft basieren diese Tests auf konkreten Szenarien aus der Lastenspezifikation und werden mit Checklisten oder Testplänen durchgeführt.

*Beispiel:* Ein QA-Tester klickt das gesamte Kaufszenario durch: Produkt auswählen, in den Warenkorb legen, bezahlen, Bestellbestätigung prüfen.

---

## Aufgabe 2

**Beispiel für einen SW-Fehler (Bug):**

Der Therac-25-Fall (1985–1987) ist ein tragisches Beispiel. Ein softwaregesteuerter Strahlentherapeut hat Patienten versehentlich eine tödliche Dosis Strahlung verabreicht. Die Ursache war ein Race Condition-Fehler in der Software, der dazu führte, dass die Maschine im manuellen Modus die falsche Strahlungsmodi freigab.

**Beispiel für einen SW-Mangel:**

Ein Passwort-Reset einer Web-App, bei dem der Reset-Link im Klartext in der URL steht und somit im Browserverlauf sichtbar ist. Das ist kein klassischer Programmierfehler, sondern ein Designmangel – die Sicherheitsanforderungen wurden nicht ordnungsgemäss umgesetzt.

**Beispiel für hohen Schaden:**

Die europäische Ariane-5-Rakete explodierte 1996, 37 Sekunden nach dem Start. Ein 64-Bit-Float-Wert wurde in eine 16-Bit-Integer-Variable gespeichert, was einen Überlauf verursachte. Der Schaden belief sich auf rund 370 Millionen Dollar. Das Problem war, dass Code aus der Ariane 4 wiederverwendet wurde, der für die neuen Werte der Ariane 5 nicht mehr passte.

---

## Aufgabe 3

Die Implementierung des `calculatePrice`-Methods und der dazugehörige Testtreiber befinden sich in der Datei `Preisberechnung.java`. Der Testtreiber ruft die Methode mit verschiedenen Testfällen auf und prüft, ob die Ergebnisse stimmen.

---

## Aufgabe 3 – Bonus

Der Fehler im Code liegt in der Reihenfolge der Bedingungen: Es wird zuerst geprüft, ob drei oder mehr Extras vorhanden sind, und wenn ja, wird ein Rabatt von 10% vergeben. Die Folge-Abfrage, ob fünf oder mehr Extras vorhanden sind, wird danach nie erreicht, weil jede Zahl, die mindestens fünf ist, automatisch auch mindestens drei ist. Der 15%-Rabatt kommt somit nie zum Tragen. Man müsste die Bedingungen einfach in umgekehrter Reihenfolge prüfen – zuerst auf fünf oder mehr, dann auf drei oder mehr.
