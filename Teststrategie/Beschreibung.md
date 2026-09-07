# Beschreibung Teststrategie

Diese Datei erklärt alle drei Übungen der Teststrategie-Aufgabe und kann als Grundlage für die Präsentation beim Dozenten dienen.

---

## Überblick: Was ist eine Teststrategie?

Beim Testen von Software reicht es nicht aus, einfach drauflos zu testen. Es braucht eine klare Strategie, die festlegt:

- **Was** wird getestet (Testobjekte, Testfälle)
- **Wie** wird getestet (Testmethoden: Black-Box, White-Box)
- **Wo** wird getestet (Testumgebung)

Testfälle können funktional sein (testet, ob die Software das Richtige tut) oder nicht-funktional (testet, wie gut sie es tut – z. B. Performance). In diesem Modul konzentrieren wir uns auf funktionale Testfälle.

Ausserdem unterscheidet man zwischen abstrakten und konkreten Testfällen:
- **Abstrakte Testfälle** verwenden logische Operatoren wie >, <, =, um Regeln zu beschreiben.
- **Konkrete Testfälle** verwenden echte Werte (z. B. 15'000 CHF), um das Resultat konkret nachrechnen zu können.

---

## Übung 1 – Rabattregeln Autohaus

### Was war die Aufgabe?

Aus der Beschreibung einer Verkaufssoftware mit Rabattregeln (zwei Varianten von Testfällen ableiten): eine abstrakte Tabelle mit logischen Operatoren und eine konkrete mit echten Zahlen.

### Die Rabattregeln im Überblick

Die Software berechnet Rabatte für einen Autoverkauf anhand des Kaufpreises:

- Weniger als 15'000 CHF: kein Rabatt
- 15'000 bis 20'000 CHF: 5% Rabatt
- Über 20'000 bis unter 25'000 CHF: 7% Rabatt
- Ab 25'000 CHF: 8,5% Rabatt

### Was wurde gemacht?

Für jede Regelskala wurde ein Testfall definiert, der sowohl die untere als auch die obere Grenze abdeckt. Dies nennt man Grenzwerttest.

Abstraktes Beispiel: «Wenn der Preis zwischen 15'000 und 20'000 liegt, beträgt der Rabatt 5%» – hier steht noch keine konkrete Zahl drin, sondern nur die Logik.

Konkretes Beispiel: «Kaufpreis = 20'000 CHF → Rabatt 5% → Endpreis = 19'000 CHF» – hier rechnet man das Resultat explizit nach.

Das ist wichtig, weil man mit konkreten Werten die Erwartung genau überprüfen kann und weiss, ob die Software stimmt.

### Wie würde ich das erklären?

«Wir haben zuerst die Regeln aus dem Text herausgelesen und die Grenzwerte identifiziert. Daraus haben wir abstrakte Testfälle formuliert, die die Logik abdecken, und dann konkrete Eingabewerte gewählt, bei denen wir das Ergebnis selbst nachrechnen können. Das gibt uns eine klare Erwartung, gegen die wir später testen können.»

---

## Übung 2 – Autovermietung (Europcar.ch)

### Was war die Aufgabe?

Man soll sich eine Website für Autovermietungen aussuchen (wir haben Europcar.ch gewählt) und 5 funktionale Black-Box-Testfälle definieren.

### Was sind Black-Box-Testfälle?

Black-Box heisst: Man kennt den Code nicht (oder schaut nicht hin). Man testet nur als Benutzer über die Oberfläche, ob das richtige Verhalten kommt. Man gibt etwas ein und schaut, was passiert.

### Was wurden die 5 Testfälle?

Wir haben die Kernfunktionen der Plattform abgedeckt:

1. **Fahrzeugsuche:** Man gibt einen Standort und einen Zeitraum ein und prüft, ob passende Fahrzeuge angezeigt werden.
2. **Fahrzeugdetails:** Man wählt ein Fahrzeug aus und prüft, ob die korrekte Detailseite mit Preis und Ausstattung kommt.
3. **Buchung abschliessen:** Man füllt den gesamten Buchungsprozess aus und prüft, ob eine Buchungsnummer kommt.
4. **Preisberechnung:** Man verifiziert, dass der Gesamtpreis (Tagespreis × Dauer + Zusatzleistungen) korrekt berechnet wird.
5. **E-Mail-Bestätigung:** Nach der Buchung prüft man, ob eine Bestätigungs-E-Mail eintrifft.

### Warum genau diese 5?

Diese Testfälle decken die kritischen Geschäftsprozesse ab: Suche → Auswahl → Buchung → Preis → Bestätigung. Ohne diese würde die Plattform nicht funktionieren. Die weiteren Details (Profil anlegen, Passwort ändern) sind nachrangig.

### Wie würde ich das erklären?

«Wir haben die Website als Black-Box getestet, das heisst, wir kennen den Code nicht, sondern testen nur über die Benutzeroberfläche. Unser Fokus lag auf den Kernprozessen: Was passiert, wenn der Kunde sucht, bucht und die Bestätigung erhält? Die 5 Testfälle decken diese kritische Kette ab. Jeder Testfall beschreibt, was man als Benutzer macht und was man als Resultat erwartet.»

---

## Übung 3 – Bank-Software

### Was war die Aufgabe?

Die Bank-Software herunterladen, lokal zum Laufen bringen, sich den Code ansehen und dann:
- Black-Box-Testfälle als Benutzer identifizieren
- White-Box-Testfälle aus dem Code ableiten
- Verbesserungen und Best Practices am Code vorschlagen

### Wie startet man die Anwendung?

Es gibt zwei Varianten:
- **Variante A (empfohlen):** Im Terminal `cd Teststrategie/bank-software`, dann `javac` zum Kompilieren und `java` zum Starten. Die Anleitung steht in `Teststrategie/Anleitung.md`.
- **Variante B:** Den Maven-Ordner `Teststrategie/bank-software-mvn/` in IntelliJ öffnen und den `Main`-Class starten.

### Black-Box-Testfälle (17 Stück)

Als Benutzer kann man die Konsole bedienen und folgende Funktionen testen:

| Bereich | Getestet wird |
|---------|---------------|
| Start | Programm startet und zeigt Menü |
| Konten anzeigen | Alle 5 Beispielkonten erscheinen mit Nummer |
| Konto auswählen | Kontodetails werden angezeigt |
| Konto auswählen (ungültig) | Fehlermeldung bei nicht vorhandener Nummer |
| Konto erstellen | Neues Konto wird angelegt |
| Einzahlen | Kontostand erhöht sich |
| Einzahlen (negativer Betrag) | Demonstriert einen Bug (Kontostand sinkt fälschlicherweise) |
| Abheben (genügend Guthaben) | Kontostand sinkt korrekt |
| Abheben (nicht genügend Guthaben) | Fehlermeldung «Kontostand zu niedrig» |
| Überweisung | Betrag wird zwischen zwei Konten verschoben |
| Überweisung auf dasselbe Konto | Fehlermeldung |
| Überweisung auf unbekanntes Konto | Fehlermeldung |
| Überweisung mit Währungswechsel | Wechselkurs wird angewendet |
| Wechselkurs abfragen | Kurs wird angezeigt (braucht Internet) |
| Konto löschen | Mit Bestätigung wird das Konto entfernt |
| Ungültige Eingabe | Fehlermeldung, Menü wiederholt sich |
| Beenden | «Auf Wiedersehen!» und Programm endet |

### White-Box-Testfälle (Code-Pfad-Test)

Da der Code sichtbar ist, kann man gezielt Pfade durch den Code testen. Wichtige Methoden:

- **Account.withdraw()** hat zwei Verzweigungen: Einzahlung möglich (true) oder nicht (false). Beide Pfade müssen getestet werden.
- **Account.deposit()** ist kritisch, weil negative Beträge nicht abgefangen werden.
- **Bank.getAccount()** gibt `null` zurück, wenn das Konto nicht existiert – das muss man testen.
- **Counter.convertCurrency()** hat für jede Währungskombination einen eigenen Code-Pfad plus einen Fall ohne Umrechnung.
- **Counter.transferAmount()** kombiniert Abhebung, Währungsumrechnung und Einzahlung – ein集成ierter Testfall.
- **Counter.createAccount()** validiert die Währungsabkürzung mit Regex und fängt unbekannte Währungen mit Fallback auf USD ab.
- **ExchangeRateOkhttp.getExchangeRate()** liefert bei Erfolg den Kurs, bei Fehler 0.0.

### Was wurde am Code verbessert? (10 Punkte)

1. **API-Key ist im Quelltext:** Der Schlüssel für den Wechselkurs-Service steht direkt im Code. Das ist ein Sicherheitsleck und gehört in eine Konfigurationsdatei.

2. **Fehler in der Logik bei Einzahlung:** Wenn man einen negativen Betrag einzahlt, wird dieser einfach akzeptiert – der Kontostand sinkt. Es fehlt eine Validierung.

3. **Fehlerhafte Reihenfolge der Bedingungen:** Analog zum Bonus aus dem Grundlagen-Auftrag: Wird zuerst `>=3` geprüft, kommt `>=5` nie zum Zuge. Die Reihenfolge muss umgekehrt werden.

4. **Zu grobe Exception-Behandlung:** Überall steht `catch(Exception)`. Das verschluckt Fehler und macht das Debuggen schwer.

5. **Tippfehler:** `AccountExeption` muss heissen `AccountException`.

6. **Absturz bei leerer Eingabe:** `input.substring(0,1)` wirft eine Exception, wenn der Benutzer nichts eingibt.

7. **Hardcoded Wechselkurse:** Die Kurse sind fest verdrahtet. Besser: aus einer Datenbank oder API lesen.

8. **NullPointerException-Gefahr:** `getAccount()` gibt `null` zurück, aber `printAccountDetails()` prüft nicht auf `null`, bevor es `a.getId()` aufruft.

9. **Statischer ID-Zähler:** Der Zähler in `Account` ist statisch und nicht thread-safe.

10. **System.exit(0):** Beendet das Programm abrupt. Besser: sauberes Durchlaufen des Menüs.

### Wie würde ich das erklären?

«Wir haben die Anwendung zuerst als Benutzer getestet – man startet das Programm und probiert verschiedene Menüoptionen aus. Daraus entstanden 17 Black-Box-Testfälle, die alle wichtigen Funktionen abdecken, inklusive Fehlerfälle wie ungültige Eingaben und leere Felder.

Danach haben wir den Code angeschaut und gesehen, welche Methoden sich für White-Box-Tests eignen – also wo es Verzweigungen gibt, die unterschiedlich getestet werden müssen. Besonders interessant ist das Verhalten bei ungültigen Werten (negativer Einzahlungsbetrag, leere Eingabe).

Als Verbesserung haben wir zudem ein Sicherheitsleck (API-Key im Code), einen Bug (negative Einzahlung erlaubt), fehlende Validierung bei Eingaben, einen Tippfehler in einer Klassennamen und allgemeine Code-Qualitätsprobleme identifiziert.»

---

## Zusammenhang mit den Lernzielen

| Lernziel | Wo angekommen |
|----------|----------------|
| Elemente einer Teststrategie kennen | Übung 1: Was genau wird getestet (Rabattregeln), Testobjekt (Berechnungsfunktion) |
| Abstrakte vs. konkrete Testfälle | Übung 1: Tabellen mit logischen Operatoren vs. konkreten CHF-Werten |
| Funktionale vs. nicht-funktionale Tests | Übung 2: Wir testen nur funktionale Aspekte der Autovermietung |
| Testmethoden erklären | Übung 3: Black-Box (Code unsichtbar) vs. White-Box (Code sichtbar, Pfad-Test) |