# Testkonzept Health Leveling

Aufbau nach der Vorlage IEEE 829 aus dem Modul 450. Die Teststrategie in Kurzform steht in der
[Projekt-README](../README.md#testing). Dieses Dokument beschreibt sie ausführlicher.

## 1. Introduction

Health Leveling übersetzt Gesundheitsdaten (Schritte, Schlaf, Aktivitäten, Mahlzeiten, Getränke, Gewicht) in XP und
Levels. Die Daten kommen aus drei Quellen: manuelle Eingabe, Datei-Import (CSV und JSON) und die Google Health API.

Ziel der Tests ist, dass:
- die XP- und Level-Berechnung genau den Regeln im [Backend-README](../health-leveling/backend/README.md) entspricht,
- ungültige Daten nie gespeichert werden und die Benutzerin oder der Benutzer eine verständliche Rückmeldung bekommt,
- die Applikation weiterläuft, wenn Google nicht erreichbar ist oder die API abgestellt wird,
- jede Änderung automatisch in der Pipeline geprüft wird, bevor sie auf main kommt.

## 2. Test Items

| Testobjekt | Ort |
|---|---|
| Backend (Spring Boot 4, Java 21) | health-leveling/backend |
| Rechenlogik: XpCalculator, LevelCalculator, StreakCalculator, DayReview | Paket leveling |
| Services: Day, Entry, Import, Sync, Profile | Pakete day, entry, importer, google, leveling |
| REST-Endpunkte | alle Controller unter /api |
| Google-Anbindung: GoogleHealthClient hinter dem Interface HealthDataClient | Paket google |
| Frontend (Angular 21): Seiten, Validierung, ApiService | health-leveling/frontend/src/app |

## 3. Features to be tested

| Feature | Testebene |
|---|---|
| XP für Schritte, Schlaf, Aktivitäten, Mahlzeiten, Getränke, Gewicht inkl. Obergrenzen | Unit (parametrisiert) |
| Level aus Gesamt-XP, XP bis zum nächsten Level, Fortschritt in Prozent | Unit |
| Tagesrückblick: Morgen, Mittag, Abend, Tagesbonus, Kalorien-Abzug | Unit |
| Streak: Bonus alle 7 Tage, Lücke setzt zurück, Zählung bis gestern | Unit |
| Achievements | Unit mit Mocks |
| Validierung der Tageswerte und Einträge (Backend und Frontend) | Unit, Integration |
| Import von CSV und JSON inkl. fehlerhafter Dateien | Unit mit Testdateien, Integration |
| Synchronisation mit Google inkl. Fehlerfälle | Unit mit Mocks, gemockter HTTP-Server |
| Alle REST-Endpunkte mit Statuscodes 200, 400, 404, 503 | Integration |
| Profil wird nach Import, Eintrag und Löschen richtig neu berechnet | Integration |
| Wichtigste Abläufe im Browser: Profil, Navigation, Erfassen, Fehler, Diagramm, Import, Backend ausgefallen | E2E |

## 4. Features not to be tested

| Feature | Begründung |
|---|---|
| Echte Verbindung zur Google Health API | Tests dürfen nicht von externen Diensten und echten Gesundheitsdaten abhängen. Einmal von Hand mit dem api-explorer geprüft. |
| PostgreSQL | Alle automatischen Tests laufen mit H2. PostgreSQL wurde von Hand mit Docker geprüft. |
| Aussehen und Styling | Kein Fokus des Moduls, ändert sich häufig. |
| Performance und Last | Eine Person, wenige hundert Datensätze, kein Risiko. |
| Sicherheit und Anmeldung | Es gibt nur ein Profil und keine Anmeldung. |

## 5. Approach

**Testpyramide:** viele Unit Tests, weniger Integration Tests, wenige E2E-Tests.

| Ebene | Werkzeuge | Umfang |
|---|---|---|
| Unit Backend | JUnit 5, AssertJ, Mockito | 116 Tests |
| Integration Backend | Spring Boot Test, MockMvc, H2, @DataJpaTest | 12 Tests |
| Unit Frontend | Vitest, Angular TestBed, HttpTestingController | 54 Tests |
| E2E Frontend | Playwright (Chromium) | 7 Tests |

**TDD:** Die Rechenlogik (XP, Level, Streak, Tagesrückblick) wurde nach Red-Green-Refactor entwickelt, weil die Regeln
vorher feststanden und sich direkt als Testfälle aufschreiben liessen.

**Grenzwertanalyse:** Für jede Regel wird genau auf der Grenze, knapp darunter und knapp darüber getestet. Beispiele:
0, 999, 1000, 9999, 10'000, 20'000 und 35'000 Schritte; Schlaf-Score 0, 100 und 101; Datum heute und morgen.

**Äquivalenzklassen:** gültige und ungültige Eingaben pro Feld, zum Beispiel leere Zelle, 0, negative Zahl, Text statt
Zahl, Dezimalzahl.

**Mocking:**
- Mockito ersetzt Repositories und den HealthDataClient in den Service-Tests.
- MockRestServiceServer ersetzt Google beim GoogleHealthClient. Die Antworten sind JSON-Dateien im echten Format.
- Im Frontend fängt der HttpTestingController die HTTP-Anfragen ab. In den E2E-Tests beantwortet Playwright alle
  Anfragen an /api im Browser. Ein Backend wird nicht gebraucht.
- In den Integration Tests sind die Google-Zugangsdaten und die automatische Synchronisation fest abgeschaltet.

**Testdaten:** Beispieldateien unter backend/src/test/resources (Import und Google-Antworten). Die Integration Tests
laufen in einer Transaktion, die nach jedem Test zurückgerollt wird.

## 6. Item pass/fail criteria

- Ein Testfall ist bestanden, wenn alle Assertions erfüllt sind.
- Ein Stand gilt als bestanden, wenn alle Tests aller Ebenen grün sind. Es gibt keine erlaubten Fehler.
- Die Coverage im Backend soll nicht unter 90 % der Zeilen fallen. Sie wird nicht als hartes Kriterium erzwungen,
  sondern im Report geprüft.

## 7. Suspension criteria and resumption requirements

- Schlägt der Build fehl, laufen keine Tests (die Test-Stage hängt von der Build-Stage ab).
- Schlägt ein Test fehl, wird nicht deployed. Weiter geht es, wenn der Fehler behoben ist und die Pipeline wieder
  grün ist.

## 8. Test Deliverables

| Ergebnis | Ort |
|---|---|
| Testkonzept | dieses Dokument |
| Testfälle | Testklassen und Spec-Dateien im Code |
| Testreport Backend (Surefire) | target/reports/surefire.html, Artefakt und GitHub Pages |
| Coverage-Report (JaCoCo) | target/site/jacoco, Artefakt und GitHub Pages |
| E2E-Report (Playwright) | playwright-report, Artefakt und GitHub Pages |
| JUnit-Dateien Frontend | reports, Artefakt |
| Testübersicht pro Lauf | GitHub Actions, Checks beim Lauf |

## 9. Testing Tasks

1. Testkonzept und Grenzwerte pro Regel festlegen.
2. Rechenlogik mit TDD entwickeln.
3. Service-Tests mit Mocks schreiben.
4. Integration Tests für alle Endpunkte schreiben.
5. Frontend-Validierung und Seiten testen.
6. E2E-Tests für die wichtigsten Abläufe schreiben.
7. Pipeline mit Build, Test und Deploy einrichten.
8. Reports prüfen und Lücken mit zusätzlichen Tests schliessen.

## 10. Environmental Needs

- JDK 21 und Maven Wrapper für das Backend, Node 22 für das Frontend.
- Chromium für die E2E-Tests. Lokal wird er von Playwright installiert, in der Pipeline samt Systembibliotheken.
- Keine Datenbank nötig, H2 läuft im Speicher.
- Keine Google-Zugangsdaten nötig.
- GitHub Actions mit aktiviertem GitHub Pages (Quelle: GitHub Actions) für die Deploy-Stage.

## 11. Responsibilities

Felipe Pereira: Planung, Testfälle, Umsetzung und Auswertung. KI-Unterstützung ist in der
[README](../README.md#ki-nutzung) deklariert.

## 12. Schedule

Siehe Tabelle «Planung» in [BESCHREIBUNG.md](../BESCHREIBUNG.md#planung). Die Tests entstehen jeweils zusammen mit dem
Feature, die Pipeline läuft ab Phase 1 bei jedem Push.

## 13. Risks and contingencies

| Risiko | Massnahme |
|---|---|
| Google stellt die API ab oder ändert sie | Google steckt hinter dem HealthDataClient, der Datei-Import funktioniert unabhängig davon. |
| Tests hängen von der Umgebung ab (z. B. gesetzte Umgebungsvariablen) | Externe Werte werden in den Tests fest überschrieben. |
| E2E-Tests sind instabil | Backend im Browser gemockt, in der Pipeline bis zu 2 Wiederholungen. |
| H2 verhält sich anders als PostgreSQL | Von Hand mit PostgreSQL geprüft. Optional später ein Test mit Testcontainers. |
