# Teststrategie

## Übung 1 – Rabattregeln Autohaus

Aus der Beschreibung lassen sich folgende Rabattregeln ableiten:

| Kaufpreis | Rabatt |
|-----------|--------|
| unter 15'000 CHF | kein Rabatt |
| 15'000 bis 20'000 CHF | 5% |
| über 20'000 bis unter 25'000 CHF | 7% |
| ab 25'000 CHF | 8,5% |

### Abstrakte Testfälle

Bei abstrakten Testfällen verwenden wir keine konkreten Zahlen, sondern logische Operatoren (grösser, kleiner, gleich), um die Grenzen der Regeln abzudecken.

| ID | Kaufpreis (Bedingung) | Erwarteter Rabatt | Erwartetes Resultat |
|----|----------------------|-------------------|----------------------|
| A1 | Preis < 15'000 | 0% | Endpreis entspricht dem Kaufpreis |
| A2 | Preis = 15'000 (Grenzwert unten) | 5% | Endpreis = Kaufpreis minus 5% |
| A3 | 15'000 < Preis ≤ 20'000 | 5% | Endpreis = Kaufpreis minus 5% |
| A4 | Preis = 20'000 (Grenzwert oben) | 5% | Endpreis = Kaufpreis minus 5% |
| A5 | 20'000 < Preis < 25'000 | 7% | Endpreis = Kaufpreis minus 7% |
| A6 | Preis = 25'000 (Grenzwert oben) | 8,5% | Endpreis = Kaufpreis minus 8,5% |
| A7 | Preis > 25'000 | 8,5% | Endpreis = Kaufpreis minus 8,5% |

### Konkrete Testfälle

Bei konkreten Testfällen verwenden wir ganz konkrete Eingabewerte, damit wir das erwartete Resultat exakt nachrechnen können.

| ID | Kaufpreis (CHF) | Erwarteter Rabatt | Erwarteter Endpreis (CHF) |
|----|-----------------|-------------------|---------------------------|
| K1 | 10'000 | 0% | 10'000.00 |
| K2 | 14'999 | 0% | 14'999.00 |
| K3 | 15'000 | 5% | 14'250.00 |
| K4 | 20'000 | 5% | 19'000.00 |
| K5 | 20'001 | 7% | 18'600.93 |
| K6 | 24'999 | 7% | 23'249.07 |
| K7 | 25'000 | 8,5% | 22'875.00 |
| K8 | 30'000 | 8,5% | 27'450.00 |

---

## Übung 2 – Black-Box-Tests Autovermietung (Europcar.ch)

Als Plattform haben wir die Website von Europcar gewählt. Für den Betrieb der Plattform sind vor allem die Kernprozesse rund um die Buchung wichtig. Die fünf wichtigsten funktionalen Black-Box-Testfälle:

| ID | Beschreibung | Erwartetes Resultat | Effektives Resultat | Status | Mögliche Ursache |
|----|--------------|---------------------|---------------------|--------|------------------|
| E1 | Fahrzeugsuche: Standort (z. B. Zürich Flughafen) und Mietzeitraum eingeben | Die Suche zeigt eine Liste mit verfügbaren Fahrzeugen inkl. Preisen an | Liste mit verfügbaren Fahrzeugen wird angezeigt | OK | – |
| E2 | Ein Fahrzeug aus der Liste auswählen und Details ansehen | Detailseite mit Fahrzeugbeschreibung, Ausstattung und Preis wird angezeigt | Detailseite wird korrekt angezeigt | OK | – |
| E3 | Buchung durchführen: Fahrzeug, Zusatzleistungen (Versicherung, Kindersitz) und Zahlung | Buchung wird abgeschlossen und eine Bestätigungsseite mit Buchungsnummer erscheint | Bestätigungsseite mit Buchungsnummer erscheint | OK | – |
| E4 | Preisberechnung prüfen: Gesamtpreis für mehrere Miettage inkl. Zusatzleistungen | Der Gesamtpreis wird korrekt aus Tagespreis, Mietdauer und Zusatzleistungen berechnet | Gesamtpreis stimmt mit der manuellen Nachrechnung überein | OK | – |
| E5 | Buchungsbestätigung per E-Mail erhalten | Nach Abschluss der Buchung trifft eine Bestätigungs-E-Mail mit den Buchungsdetails ein | Bestätigungs-E-Mail trifft innert weniger Minuten ein | OK | E-Mail-Server oder Spam-Ordner |

---

## Übung 3 – Bank-Software

### Black-Box-Testfälle (als Benutzer testbar)

Die Applikation ist eine Konsolenanwendung, die ein Bankmenü anbietet. Folgende Black-Box-Testfälle lassen sich als Benutzer durchführen:

| ID | Beschreibung | Erwartetes Resultat | Effektives Resultat | Status |
|----|--------------|---------------------|---------------------|--------|
| B1 | Programm starten | Begrüssung "Willkommen am Schalter 1" und das Hauptmenü mit den Optionen erscheinen | | offen |
| B2 | Option "a" (alle Konten anzeigen) eingeben | Liste aller Konten mit Nummer und Nachname wird angezeigt | | offen |
| B3 | Option "e" (Konto erstellen) und einen Nachnamen eingeben | Neues Konto wird mit angegebener Nummer und Währung angelegt | | offen |
| B4 | Eine gültige Kontonummer (z. B. 1) eingeben | Kontodetails und das Bearbeitungsmenü werden angezeigt | | offen |
| B5 | Eine ungültige Kontonummer (z. B. 99) eingeben | Fehlermeldung "Ein Konto mit dieser Nummer ist nicht vorhanden!" erscheint | | offen |
| B6 | Einzahlung ("e") mit einem gültigen Betrag | Kontostand erhöht sich um den eingegebenen Betrag | | offen |
| B7 | Einzahlung mit einem negativen Betrag | Negativer Betrag wird (fälschlicherweise) akzeptiert, Kontostand sinkt | | offen |
| B8 | Abhebung ("a") mit genügend Guthaben | Kontostand sinkt um den abgehobenen Betrag | | offen |
| B9 | Abhebung mit mehr Geld als vorhanden | Fehlermeldung "Kontostand zu niedrig" erscheint | | offen |
| B10 | Überweisung ("ü") auf ein anderes Konto mit genügend Guthaben | Betrag wird vom einen aufs andere Konto verschoben | | offen |
| B11 | Überweisung auf das gleiche Konto | Fehlermeldung "Bitte ein anderes Konto auswählen" erscheint | | offen |
| B12 | Überweisung auf ein nicht vorhandenes Konto | Fehlermeldung, dass dieses Konto nicht existiert | | offen |
| B13 | Überweisung zwischen Konten mit unterschiedlichen Währungen (z. B. CHF nach EUR) | Der Betrag wird mit dem hinterlegten Wechselkurs umgerechnet | | offen |
| B14 | Option "w" (Wechselkurs) mit der Eingabe "CHF USD" | Der aktuelle Wechselkurs wird angezeigt | | offen |
| B15 | Option "l" (Konto löschen) mit Bestätigung "j" | Konto wird aus der Liste entfernt und eine Bestätigung ausgegeben | | offen |
| B16 | Ungültige Eingabe im Hauptmenü (z. B. "x") | Fehlermeldung erscheint und das Menü wird erneut angezeigt | | offen |
| B17 | Option "q" (Beenden) eingeben | "Auf Wiedersehen!" wird ausgegeben und die Anwendung endet | | offen |

### White-Box-Testfälle

Da der Code sichtbar ist, können gezielt einzelne Methoden durchgetestet werden (Code-Pfad-Test). Geeignete Methoden:

| Methode | Was wird getestet |
|---------|-------------------|
| `Account.withdraw(double)` | Beide Verzweigungen: kein genügendes Guthaben (false) und genügend Guthaben (true) |
| `Account.deposit(double)` | Kontostand wird erhöht; kritisch: negative Beträge werden nicht abgefangen |
| `Account.getBalance()` / `getCurrency()` | Rückgabe der gesetzten Werte nach Ein- und Auszahlungen |
| `Bank.getAccount(int)` | Verzweigung: Konto gefunden vs. keine Übereinstimmung (Rückgabe null) |
| `Bank.createAccount(...)` | Konto wird angelegt und der ID-Zähler hochgezählt |
| `Bank.deleteAccount(Account)` / `getNumberOfAccounts()` | Anzahl der Konten nimmt ab bzw. gibt die korrekte Grösse zurück |
| `Counter.transferAmount(...)` | Gesamte Kette: Abhebung, Währungsumrechnung und Einzahlung auf dem Zielkonto |
| `Counter.convertCurrency(...)` | Jede Währungskombination (USD→CHF, USD→EUR, CHF→USD) sowie der Fall ohne Umrechnung |
| `Counter.createAccount()` | Validierung der Währungsabkürzung (Regex) und der Fallback auf USD bei unbekannter Währung |
| `Counter.getConfirmation()` | Rückgabe true bei "j", false bei jeder anderen Eingabe |
| `ExchangeRateOkhttp.getExchangeRate(...)` | Erfolgreiche Antwort liefert den Kurs, ein Fehler liefert 0.0 |

Hinweis: Einige dieser Methoden sind privat (z. B. `convertCurrency`, `deposit`, `withdraw` in `Counter`). Für White-Box-Tests müsste man die Sichtbarkeit anpassen (package-private) oder über die öffentlichen Methoden testen.

### Verbesserungen und Best Practices

- **API-Key ist hart im Code:** In `ExchangeRateOkhttp` steht der Zugriffsschlüssel direkt im Quelltext. Das ist ein Sicherheitsleck – der Schlüssel gehört in eine Umgebungsvariable oder eine Konfigurationsdatei und darf nie ins Repository.
- **Fehler im Einzahlungslogik:** `deposit()` validiert den Betrag nicht – ein negativer Betrag wird schlicht akzeptiert und reduziert den Kontostand.
- **Zu grobe Exception-Verarbeitung:** Überall wird mit `catch(Exception)` gearbeitet. Das verschluckt Fehler und erschwert das Debuggen. Konkrete Exception-Typen wären besser.
- **Tippfehler in der Klasse:** `AccountExeption` müsste korrekt `AccountException` heissen.
- **Absturz bei leerer Eingabe:** `input.substring(0, 1)` wirft eine Exception, wenn nichts eingegeben wird. Vorher sollte auf leere Eingaben geprüft werden.
- **Magic Numbers bei den Wechselkursen:** Die Kurse (1.11, 0.91, 0.9) sind hart verdrahtet. Solche Werte gehören in eine zentrale Konfiguration oder eine externe Datenquelle, damit sie gepflegt werden können.
- **Der statische Zähler in `Account`:** `counter` ist statisch und nicht thread-sicher. Zudem beginnt die Nummerierung bei jedem Programmstart wieder bei eins.
- **NullPointerException-Gefahr:** `getAccount()` gibt `null` zurück, wenn das Konto nicht existiert. `printAccountDetails()` ruft danach aber `a.getId()` auf, ohne auf `null` zu prüfen – das führt zum Absturz.
- **Programmbeendigung:** `System.exit(0)` in `Main` beendet das Programm abrupt. Besser wäre ein sauberes Durchlaufen des Menüs bis zum Ende.
- **Konsistente Kommentare:** Deutsch und Englisch sind gemischt, und es gibt offene TODO-Kommentare. Ein einheitlicher Stil wäre sauberer.
- **Testbarkeit:** Für Unit- und White-Box-Tests sollten die relevanten Methoden nicht alle privat sein, sondern durch die öffentliche Schnittstelle oder in einem eigenen Testpackage testbar sein.