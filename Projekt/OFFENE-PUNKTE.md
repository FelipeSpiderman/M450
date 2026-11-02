# Offene Punkte bis zur Abgabe

Abgleich des Projekts mit den Vorgaben des Moduls (Bewertungsrubrik, Eckdaten, CI/CD, Code Reviews, Auswertung) und mit der eigenen [BESCHREIBUNG.md](BESCHREIBUNG.md). Stand: 29.10.2026.

## Was bereits erfüllt ist

| Anforderung | Nachweis |
|---|---|
| Funktionen MVP und alle Erweiterungen | Profil, Tageswerte, Einträge, Import, Tagesübersicht, Google-Sync, Diagramm, Streaks, Achievements |
| Unit Tests | Backend 128 Tests (JUnit 5), Frontend 54 Tests (Vitest) |
| Integration Tests | `HealthLevelingIntegrationTest`, `EntryRepositoryTest` (Spring Boot Test) |
| Schnittstellen gemockt | Mockito im Backend, `HttpTestingController` und `page.route` im Frontend |
| Testreports und Coverage | Surefire-Report und JaCoCo als Artefakt in GitHub Actions |
| TDD | XP- und Level-Logik (laut Beschreibung) |
| Planung und Architektur | Tabelle «Planung» und Mermaid-Diagramm in der BESCHREIBUNG.md |
| KI-Deklaration | Abschnitt «KI-Nutzung» vorhanden |

## Was fehlt oder angepasst werden muss

Die Reihenfolge entspricht der Priorität. Jeder Punkt bekommt einen eigenen Branch ab `Project` und wird per Pull Request in `Project` gemerged.

### 1. Pipeline läuft nicht auf `main` und hat keine Deploy-Stage

**Branch:** `ci/pipeline-main-deploy`

Die Rubrik verlangt: «Tests werden in einer Pipeline beim Deployen auf `main` automatisch ausgeführt und reportet.» Laut `ci-cd.md` braucht es die Stages **Build**, **Test** und **Deploy** (Deploy = Testresultate publizieren, z. B. mit Pages).

Aktuell:
- `projekt.yml` startet nur bei Push oder PR auf `Project`, nicht auf `main`.
- Build und Test laufen in einem einzigen Schritt.
- Es gibt keine Deploy-Stage. Die Reports liegen nur als Artefakt vor.

Zu tun:
- `main` zu den Triggern in `projekt.yml` hinzufügen.
- Build und Test als eigene Schritte oder Jobs aufteilen.
- Deploy-Job nur auf `main`: JaCoCo- und Surefire-Report auf GitHub Pages veröffentlichen, nur wenn die Tests grün sind.

### 2. Testkonzept fehlt

**Branch:** `docs/testkonzept`

Die BESCHREIBUNG.md verlinkt auf `docs/testkonzept.md`, die Datei existiert aber nicht. Die Eckdaten verlangen ein kleines Testkonzept nach der Vorlage im Modul (IEEE 829).

Zu tun: `Projekt/docs/testkonzept.md` erstellen, mit den Abschnitten der Vorlage: Introduction, Test Items, Features to be tested, Features not to be tested, Approach, Item pass/fail criteria, Test Deliverables, Testing Tasks, Environmental Needs, Schedule.

### 3. Projekt-README und Beschreibung anpassen

**Branch:** `docs/projekt-readme`

- Die Rubrik spricht vom **README.md des Projekts** (Teststrategie und KI-Deklaration). Im Ordner `Projekt/` gibt es aber nur `BESCHREIBUNG.md`. Am einfachsten wird sie in `README.md` umbenannt, sie wird nirgends verlinkt.
- Abschnitt «Arbeitsweise»: Dort steht, dass auf `main` gemerged wird. Tatsächlich laufen die Feature-Branches über `Project`, das am Schluss auf `main` kommt. Das so beschreiben.
- Abschnitt «CI/CD-Pipeline» an die echte Pipeline anpassen (nach Punkt 1).
- Abschnitt «KI-Nutzung»: Die Vorgabe nennt vier Rollen (Nachschlagewerk, Schreiben mit Autocomplete, Code durch einen Agenten erstellen lassen, Reviewen/Optimieren). Ehrlich festhalten, welche davon tatsächlich vorkamen, auch die Kontrolle der PRs mit Claude Code. Mit KI wird stärker nach Aufwand bewertet. Du musst den Code erklären können.
- Im Tech-Stack stehen Playwright-E2E-Tests, die laufen aber nicht in der Pipeline. Entweder in der Pipeline ergänzen (eigener Branch `ci/e2e-tests`, der Browser muss in der Pipeline samt Systembibliotheken installiert werden) oder in der Beschreibung festhalten, dass sie nur lokal laufen.
- Im Root-`README.md` des Repositories fehlt ein Abschnitt zum Projekt mit Link auf `Projekt/`.

### 4. Reflexion über TDD und Code Reviews fehlt

**Branch:** `docs/reflexion`

Die Eckdaten und `auswertungen.md` verlangen eine kurze Reflexion über TDD und Code Reviews. Das ist auch Teil der Präsentation.

Zu tun: Abschnitt «Reflexion» (in der README oder als `docs/reflexion.md`), z. B. mit diesen Fragen:
- Wo hat TDD geholfen, wo war es mühsam?
- Wie liefen die Code Reviews ab und was wurde aufgrund von Kommentaren geändert?
- Was würdest du im Berufsalltag übernehmen, was nicht?
- Die Backend-Commits sind sehr gross (rund 2800 Zeilen in 2 Commits), obwohl kleine Commits verlangt sind. Das offen ansprechen und ab jetzt klein committen.

### 5. Code Reviews mit Kommentaren

**Kein eigener Branch**, betrifft die Pull Requests der Punkte 1 bis 4.

Verlangt sind pro Teammitglied **3 Pull Requests, die aktiv kommentiert und challenged werden**. Bisher gibt es im Projekt 2 PRs (#5 Backend, #6 Frontend). Das Review muss eine Person machen, nicht eine KI.

Zu tun:
- Für die PRs aus Punkt 1 bis 4 eine Mitschülerin, einen Mitschüler oder die Lehrperson als Reviewer hinzufügen.
- Auf Kommentare antworten und Feedback mit zusätzlichen Commits im selben Branch umsetzen, erst danach mergen.

### 6. Abschluss: `Project` auf `main` mergen

**Pull Request:** `Project` → `main` (kein neuer Branch)

Erst wenn Punkt 1 bis 4 in `Project` sind. Dieser Merge löst die Pipeline mit Deploy auf `main` aus. Danach prüfen, dass die Reports auf GitHub Pages erreichbar sind, und den Link der Lehrperson geben (siehe `ci-cd.md`).

### 7. Präsentation (10 Minuten)

Nicht im Repository. Inhalt laut Eckdaten: Endprodukt kurz zeigen, Testing, Reports vorzeigen (Pages-Link aus Punkt 6), Reflexion über Code Reviews und TDD, kurzes Fazit.

## Ablauf pro Branch

1. Auf `Project` wechseln und den neuesten Stand holen.
2. Einen neuen Branch mit dem Namen aus der Liste oben erstellen.
3. Änderungen in kleinen Schritten committen und den Branch pushen.
4. Auf GitHub einen Pull Request in `Project` eröffnen und einen Reviewer hinzufügen.
5. Nach dem Merge auf GitHub wieder auf `Project` wechseln und den neuen Stand holen.

Nicht lokal in `Project` mergen, sonst entstehen wieder doppelte Merge-Commits wie beim Frontend.
