# Beschreibung Unit Testing

Diese Datei erklärt die vier Aufgaben zum Thema Unit Testing – als Grundlage zum Verständnis und für die Präsentation beim Dozenten.

---

## Was ist Unit Testing?

Unit-Tests prüfen einzelne Teile (Units) des Source Codes – meist eine einzelne Methode oder Klasse. Ziel ist es, abzusichern, dass die von den Entwicklern geschriebenen Komponenten so arbeiten, wie vorgesehen.

Wichtige Punkte aus der Theorie:

- Unit-Tests werden **automatisiert** ausgeführt (per Maven-Befehl oder Knopfdruck in der IDE).
- Sie helfen, **langfristig änderbaren Code** zu behalten und sichern **Refactorings** ab.
- Versehentliche Änderungen werden **sofort aufgedeckt**.
- Gute Unit-Tests sind **isoliert** (unabhängig von anderen Tests), **schnell**, **wiederholbar** und **leicht verständlich**.
- Sie sichern **je genau eine Eigenschaft** ab.

---

## Aufgabe 1 – Simpler Rechner

**Was war zu tun?**
Eine `Calculator`-Klasse mit den vier Grundrechenarten (+, −, ×, ÷) erstellen und dazu die passenden Unit-Tests mit JUnit 5 schreiben.

**Was haben wir gemacht?**
- Projekt `calculator/` mit Maven-Struktur (`pom.xml`, `src/main`, `src/test`) aufgesetzt.
- Klasse `Calculator` mit `add`, `subtract`, `multiply` und `divide` erstellt. Die Division durch null wirft eine `ArithmeticException`.
- Testklasse `CalculatorTest` mit **12 Tests** geschrieben, die folgende JUnit-Features nutzen:
  - `@Test` für die einzelnen Testfälle
  - `@BeforeEach` für die Vorbereitung (frisches Calculator-Objekt)
  - `@DisplayName` für verständliche Testnamen
  - `assertEquals` und `assertThrows` für die Überprüfung
  - `@ParameterizedTest` mit `@CsvSource`, um die Addition mit mehreren Werte-Paaren gleichzeitig zu testen

**Resultat:** Alle 12 Tests laufen grün.

**So startest du es:**
```
cd calculator
./mvnw test
```

---

## Aufgabe 2 – JUnit Zusammenfassung

**Was war zu tun?**
Die gängigsten JUnit-Features in einem Markdown-Dokument zusammenfassen, mit kurzen Anwendungsbeispielen und einem Referenz-Link.

**Was haben wir gemacht?**
Das Dokument `JUnit_Zusammenfassung.md` erstellt. Enthalten sind:
- `@Test`, `@BeforeEach`/`@AfterEach`-Lebenszyklus
- Assertions (`assertEquals`, `assertTrue`, `assertThrows`, ...)
- `@DisplayName`
- Parameterisierte Tests (`@ParameterizedTest`, `@CsvSource`)
- Ausführen mit Maven (`./mvnw test`)
- Code Coverage mit JaCoCo
- Referenz: Vogella-Tutorial und offizielle JUnit-5-Doku

---

## Aufgabe 3 – Banken Simulation

**Was war zu tun?**
Die Bank-Software lokal aufsetzen (wir haben das Maven-Projekt in `02_bank-vorgabe/` verwendet), den Code und das Klassendiagramm studieren und die Funktionsweise in einem Markdown-Dokument festhalten.

**Was haben wir gemacht?**
Das Dokument `Bank_Dokumentation.md` erstellt mit:
- **Klassenübersicht:** `Account` (abstrakt), `SavingsAccount`, `SalaryAccount`, `PromoYouthSavingsAccount`, `Booking`, `Bank`, `BankUtils`, zwei Comparators, `Main`.
- **Vererbungsstruktur:** `Account` → `SavingsAccount` → `PromoYouthSavingsAccount`, und `Account` → `SalaryAccount`.
- **Zusammenhänge:** Bank hält alle Konten in einer `TreeMap`, jedes Konto hält seine Buchungen, die Bank delegiert alle Ein-/Auszahlungen an die Konten.
- **Typischer Ablauf:** Konto erstellen → einzahlen → Kontostand abfragen → Top-5 ausgeben.
- **Logikdetails:** negative Beträge werden abgelehnt, Sparkonto darf nicht ins Minus, Lohnkonto bis zur Kreditlimite, Bonus beim Jugendkonto 1%.

---

## Aufgabe 4 – Unit-Tests implementieren

**Was war zu tun?**
Für die Bank-Simulation die Unit-Tests implementieren und auf eine gute Code Coverage achten.

**Was haben wir gemacht?**
Die Test-Vorlagen in `02_bank-vorgabe/src/test/java/ch/schule/bank/junit5/` befüllt:

| Testklasse | Anzahl Tests | Was getestet wird |
|------------|--------------|-------------------|
| `BookingTests` | 3 | Buchung mit Datum/Betrag, negative Beträge, print() |
| `AccountTests` | 10 | Init, Einzahlung (auch negativ/altes Datum), Abhebung, Referenzen, canTransact, print |
| `SavingsAccountTests` | 4 | Kein Überziehen, Abhebung bis zum Saldo, leere ID |
| `SalaryAccountTests` | 5 | Init, Einzahlung, Abhebung bis/w über die Kreditlimite, Überziehen |
| `PromoYouthSavingsAccountTests` | 4 | 1%-Bonus, Bonus bei krummen Beträgen, negative Einzahlung, kein Überziehen |
| `BankTests` | 12 | Konten erstellen, Ein-/Auszahlungen, unbekannte Konten, print, Gesamtbilanz, Top5/Bottom5 |

**Resultat:**
- **38 Tests, alle grün** (per `./mvnw test`).
- **Code Coverage ~90%** (Zeilen): In die `pom.xml` wurde das JaCoCo-Plugin eingebaut. Report unter `target/site/jacoco/index.html`. Nur die reine `Main`-Starterklasse ist bewusst ungetestet.

**Gute Unit-Tests im Einsatz:**
- Jeder Test ist **isoliert** (erzeugt sich sein eigenes Konto/Bank-Objekt).
- Jeder Test sichert **genau eine Eigenschaft** ab.
- Es werden **keine Getter/Setter "nur so"** getestet, sondern die fachliche Logik (Ein-/Auszahlung, Limiten, Bonus).

---

## Zusammenhang mit den Lernzielen

| Lernziel | Wo erreicht |
|----------|-------------|
| Ich weiss, was Unit-Tests sind | Theorie + Aufgaben 1 und 4 |
| Ich weiss, wozu es Unit-Tests braucht | Dokumentation: Sicherheit bei Refactorings, sofortiges Aufdecken von Fehlern |
| Ich weiss, was gute Unit-Tests auszeichnet | Isoliert, schnell, eine Eigenschaft pro Test – in der Praxis umgesetzt |
| Erste praktische Erfahrung mit Unit-Tests | Calculator + Bank (50 Tests total) |
| Unit-Tests in einer CI/CD-Pipeline automatisieren | Tests laufen im Maven-Build (`./mvnw test`) – ideal für CI/CD |