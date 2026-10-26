# Testabdeckung – was vorhanden ist und was fehlt

Überprüfung der Tests im Projekt Health Leveling gegen die Anforderungen aus [`BESCHREIBUNG.md`](BESCHREIBUNG.md), dem [Backend-README](health-leveling/backend/README.md) und dem [Frontend-README](health-leveling/frontend/README.md).

Stand der Prüfung (Oktober 2026):

| Teil | Branch / Commit | Tests | Ergebnis |
|---|---|---|---|
| Backend | `feat/backend` (c01e170) | 13 Testklassen, 128 Tests | alle grün |
| Frontend Unit | `feat/frontend` (777e2a8) | 9 Spec-Dateien, 48 Tests (Vitest) | alle grün |
| Frontend E2E | `feat/frontend` (777e2a8) | 7 Tests (Playwright) | alle grün |

## Kurzfazit

Die Testfälle, die in der Teststrategie verlangt werden, sind vorhanden. Die Lücken liegen nicht bei fehlenden Testfällen, sondern bei der **Testumgebung** (Isolation, Pipeline) und beim **Nachweis bzw. der Dokumentation**.

## Was abgedeckt ist

| Anforderung | Test(s) |
|---|---|
| Unit Tests: XP-Berechnung und Level-Aufstieg | `XpCalculatorTest`, `LevelCalculatorTest`, `DayReviewTest`, `StreakCalculatorTest` |
| Unit Tests: Validierung der Eingaben | `DailyValuesTest`, `EntryServiceTest`, Fehlerfälle (400) im `HealthLevelingIntegrationTest` |
| Unit Tests: Parsing von JSON und CSV | `ImportServiceTest` mit Beispieldateien in `src/test/resources/import/` |
| Unit Tests mit Mocks (Repository, API) | `DayServiceTest`, `EntryServiceTest`, `ImportServiceTest`, `SyncServiceTest`, `ProfileServiceTest` (Mockito) |
| Integration Tests: Controller, Service und Datenbank über REST | `HealthLevelingIntegrationTest` (alle Endpunkte, H2), `EntryRepositoryTest` (`@DataJpaTest`) |
| Google Health API wird gemockt | `GoogleHealthClientTest` (`MockRestServiceServer`), `SyncServiceTest` (Mockito) |
| Grenzwert: 0 Schritte | `XpCalculatorTest`, `ImportServiceTest` (leere Zelle ≠ 0) |
| Grenzwert: Schlaf-Score genau 100 | `DailyValuesTest`, `XpCalculatorTest` |
| Grenzwert: leere oder fehlerhafte Importdateien | `ImportServiceTest` (leer, nur Kopfzeile, falsche Kopfzeile, falsches Datum, Text statt Zahl, kaputtes JSON, negative Schritte) |
| Leveling-Regeln aus dem Backend-README (Qualität, Übertreiben, Tagesrückblick) | `XpCalculatorTest`, `DayReviewTest` |
| Frontend: Validierung mit verständlicher Rückmeldung | `validators.spec.ts`, `messages.spec.ts`, `entries-page.spec.ts`, `days-page.spec.ts` |
| Frontend: Unit Tests mit Vitest | 9 Spec-Dateien unter `src/app/` |
| E2E Tests mit Playwright | `e2e/app.spec.ts` (API im Browser gemockt, kein Backend nötig) |
| Code Coverage mit JaCoCo, Testreports als Artefakt | `./mvnw verify` und Upload im Workflow `projekt.yml` |

## Was fehlt

### 1. Integrationstest ist nicht von Google isoliert

- **Wo:** `HealthLevelingIntegrationTest` (Backend)
- **Problem:** Der Test startet die ganze Applikation mit `@SpringBootTest`. Die `application.properties` liest `GOOGLE_REFRESH_TOKEN` aus der Umgebung. Ist diese Variable gesetzt (z. B. nachdem `api-explorer/.env` geladen wurde), ruft der Test `sync_withoutGoogleCredentials_returns503` die **echte** Google API auf.
- **Warum relevant:** Die Teststrategie verlangt, dass die Google Health API in **allen** Tests gemockt wird. Der Test kann zudem je nach Umgebung grün oder rot sein.
- **Vorschlag:** Die Werte im Test fest überschreiben:
  ```java
  @SpringBootTest(properties = {"google.health.refresh-token=", "google.health.sync-cron=-"})
  ```

### 2. E2E-Tests laufen nicht in der Pipeline

- **Wo:** `.github/workflows/projekt.yml`, Job `frontend`
- **Problem:** Die Pipeline führt nur `ng test` und `ng build` aus. Die Playwright-Tests werden nie automatisch ausgeführt.
- **Vorschlag:** Schritt mit `npx playwright install --with-deps chromium` und `npx playwright test` ergänzen und `playwright-report/` als Artefakt hochladen.

### 3. Pipeline-Stufen und Deploy fehlen

- **Wo:** `.github/workflows/projekt.yml`
- **Problem:** In der Beschreibung stehen die Stufen **build → test → report → deploy**. Deployed wird nur beim Merge auf `main` und nicht, wenn Tests fehlschlagen. Heute gibt es einen Job pro Seite (Backend, Frontend), aber keine Deploy-Stufe.
- **Vorschlag:** Jobs mit `needs:` verketten und eine Deploy-Stufe ergänzen, die nur auf `main` läuft (z. B. `.jar` und Frontend-Build als Release-Artefakt veröffentlichen).

### 4. Pipeline-Trigger passen nicht zur Arbeitsweise

- **Wo:** `.github/workflows/projekt.yml` (`on:`)
- **Problem:** Die Pipeline läuft nur bei Push oder Pull Request auf den Branch `Project`. Laut Arbeitsweise wird in Feature-Branches gearbeitet und per Merge Request auf `main` gebracht. Pushes auf `feat/backend` oder `feat/frontend` lösen keine Pipeline aus.
- **Vorschlag:** Trigger auf `main` und Feature-Branches (z. B. `feat/**`) erweitern.

### 5. Testkonzept fehlt

- **Wo:** `docs/testkonzept.md`
- **Problem:** In der Beschreibung steht: «Das ausführliche Testkonzept befindet sich unter `docs/testkonzept.md`». Die Datei existiert nicht.
- **Vorschlag:** Testkonzept erstellen: Testebenen, Werkzeuge, Grenzwerte, Mocking, Testdaten, Ein- und Ausstiegskriterien. Die Tabelle «Was abgedeckt ist» oben kann als Grundlage dienen.

### 6. TDD ist nicht nachweisbar

- **Wo:** Git-Historie von `feat/backend`
- **Problem:** Laut Beschreibung wurde die XP- und Level-Logik nach Red-Green-Refactor entwickelt. In der Historie gibt es aber nur zwei grosse Commits, die einzelnen Red- und Green-Schritte sind nicht sichtbar. In der TDD-Übung wurde das mit `docs/01-red.log` und `docs/02-green.log` gezeigt.
- **Vorschlag:** Die TDD-Zyklen im Testkonzept beschreiben oder mit Red/Green-Logs belegen. Dabei ehrlich angeben, dass dies mit KI-Unterstützung entstanden ist.

### 7. Frontend-Testreports nicht als Artefakt (optional)

- **Wo:** `.github/workflows/projekt.yml`
- **Problem:** Nur die Backend-Reports (Surefire, JaCoCo) werden hochgeladen. Die Ergebnisse von Vitest und Playwright sind nur im Log des Jobs sichtbar.
- **Vorschlag:** JUnit-Reporter für Vitest bzw. Playwright aktivieren und die Reports hochladen.

### 8. Kein automatischer Test gegen PostgreSQL (optional)

- **Wo:** Backend
- **Problem:** Alle Tests laufen mit H2. Das entspricht der Beschreibung (H2 für Tests, PostgreSQL im Betrieb). Mit PostgreSQL wurde die Applikation nur einmal von Hand geprüft.
- **Vorschlag:** Nur falls gewünscht: ein Integrationstest mit Testcontainers und PostgreSQL.

## Ebenfalls offen (keine Tests, aber in den READMEs erwähnt)

- `docs/reflexion.md` fehlt (ist laut Beschreibung für das Projektende vorgesehen).
- Der Abschnitt **KI-Nutzung** in `BESCHREIBUNG.md` entspricht nicht mehr der tatsächlichen Nutzung. Backend und Frontend wurden mit KI geschrieben.
- Die Namen der Achievements kommen auf Englisch aus dem Backend, die Oberfläche ist auf Deutsch.
