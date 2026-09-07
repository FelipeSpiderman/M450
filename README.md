# M450 – Modul 450

## Übersicht der Abgaben

Dieses Repository enthält die Lösungen zu den Aufgaben der Modul 450.

### Grundlagen

| Thema | Datei | Inhalt |
|-------|-------|--------|
| Testarten | `README.md` → Aufgabe 1 | Unit-Tests, Integrationstests, System-/Abnahmetests |
| SW-Fehler / SW-Mangel | `README.md` → Aufgabe 2 | Therac-25, Ariane 5, Passwort-Reset-Designfehler |
| Preisberechnung + Testtreiber | `Preisberechnung.java` | Kompilierter Java-Code mit 7 Testfällen |
| Bonus: Fehler im Code | `README.md` → Aufgabe 3 Bonus | Falsche Reihenfolge der Bedingungen |

**Ausführen:**
```
cd Grundlagen
javac Preisberechnung.java && java Preisberechnung
```

---

### Teststrategie

| Thema | Datei | Inhalt |
|-------|-------|--------|
| Rabattregeln Autohaus | `README.md` → Übung 1 | Abstrakte und konkrete Testfälle |
| Autovermietung Europcar | `README.md` → Übung 2 | 5 funktionale Black-Box-Testfälle |
| Bank-Software | `README.md` → Übung 3 | Black/White-Box + 10 Verbesserungen |
| Aufgabenstellung | `README.md` | Die ursprünglichen Übungs-Aufträge |
| Einrichtungsanleitung | `Anleitung.md` | Schritt-für-Schritt: Bank-Software kompilieren und starten |
| Gesamterklärung | `Beschreibung.md` | Detaillierte Erklärung aller 3 Übungen (für Präsentation) |
| Bank-Software Quellcode | `bank-software/src/` | Java-Quellcode (Account, Bank, Counter, Main, ExchangeRateOkhttp) |
| Bank-Software Maven | `bank-software-mvn/` | Alternatives Maven-Projekt |

**Bank-Software starten:**
```
cd Teststrategie/bank-software
javac -encoding UTF-8 -d classes -cp ".:./lib/gson/*:./lib/okhttp/*" src/*.java
java -cp "classes:./lib/gson/*:./lib/okhttp/*" Main
```

---

### Unit Testing

| Thema | Datei | Inhalt |
|-------|-------|--------|
| Simpler Rechner (Aufgabe 1) | `calculator/` | Calculator-Klasse + 12 JUnit-5-Tests |
| JUnit Zusammenfassung (Aufgabe 2) | `JUnit_Zusammenfassung.md` | Gängigste JUnit-5-Features mit Beispielen |
| Banken Simulation (Aufgabe 3) | `Bank_Dokumentation.md` | Funktionsweise & Zusammenhänge der Bank |
| Unit-Tests Bank (Aufgabe 4) | `02_bank-vorgabe/src/test/` | 38 Tests, Coverage ~90% |
| Gesamterklärung | `Beschreibung.md` | Detaillierte Erklärung aller 4 Aufgaben |

**Tests ausführen:**
```
cd Unit-Testing/calculator && ./mvnw test
cd Unit-Testing/02_bank-vorgabe && ./mvnw test
```
