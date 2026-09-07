# Unit Testing

## Übersicht der Abgaben

| Aufgabe | Inhalt | Datei |
|---------|--------|-------|
| Aufgabe 1 – Simpler Rechner | Calculator-Klasse mit JUnit-5-Tests | `calculator/` |
| Aufgabe 2 – JUnit Zusammenfassung | Gängigste JUnit-5-Features mit Beispielen | `JUnit_Zusammenfassung.md` |
| Aufgabe 3 – Banken Simulation | Funktionsweise der Bank dokumentieren | `Bank_Dokumentation.md` |
| Aufgabe 4 – Unit-Tests implementieren | Testfälle für die Bank (38 Tests, ~90% Coverage) | `02_bank-vorgabe/src/test/` |

## Projekte ausführen

### Calculator (Aufgabe 1)

```
cd calculator
./mvnw test
```

Ergebnis: 12 Tests, alle grün.

### Bank-Simulation (Aufgaben 3 + 4)

```
cd 02_bank-vorgabe
./mvnw test
```

Ergebnis: 38 Tests, alle grün. Coverage-Report anschliessend unter `02_bank-vorgabe/target/site/jacoco/index.html`.

Hinweis: Beide Projekte enthalten einen Maven-Wrapper (`./mvnw`), damit kein separater Maven-Install nötig ist. Die Tests lassen sich aber auch direkt in der IDE ausführen.

## Dokumente

- **Beschreibung.md** – Gesamterklärung der Unit-Testing-Aufgabe (zum Präsentieren)
- **JUnit_Zusammenfassung.md** – Aufgabe 2
- **Bank_Dokumentation.md** – Aufgabe 3