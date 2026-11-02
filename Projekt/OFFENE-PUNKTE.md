# Offene Punkte bis zur Abgabe

Abgleich des Projekts mit den Vorgaben des Moduls (Bewertungsrubrik, Eckdaten, CI/CD, Code Reviews, Auswertung).
Stand: 02.11.2026.

## Erledigt

| Anforderung | Nachweis |
|---|---|
| Teststrategie im README.md des Projekts | [README.md](README.md#testing), ausführlich in [docs/testkonzept.md](docs/testkonzept.md) |
| Unit Tests | Backend 116 (JUnit 5), Frontend 54 (Vitest) |
| Integration Tests | `HealthLevelingIntegrationTest`, `EntryRepositoryTest` (12 Tests) |
| Schnittstellen gemockt | Mockito, MockRestServiceServer, HttpTestingController, Playwright |
| Integrationstest von Google isoliert | Google-Token und automatischer Sync sind im Test fest abgeschaltet |
| Testresultate werden reportet | Testübersicht in GitHub Actions, Artefakte, GitHub Pages |
| Pipeline mit Build, Test und Deploy auf main | `.github/workflows/projekt.yml` |
| E2E-Tests in der Pipeline | Playwright mit Chromium in der Test-Stage |
| Testkonzept | [docs/testkonzept.md](docs/testkonzept.md) |
| Arbeitsweise und Pipeline in der BESCHREIBUNG.md | angepasst an Feature-Branch → Project → main |
| Projekt im Root-README verlinkt | Abschnitt «Projekt: Health Leveling» |
| Achievements auf Deutsch | wie der Rest der Oberfläche |

## Noch zu tun (nur ich kann das machen)

### 1. GitHub Pages einschalten

Einmalig auf GitHub: Settings → Pages → Source auf «GitHub Actions» stellen. Sonst schlägt die Deploy-Stage fehl.
Nach dem ersten grünen Lauf auf main steht der Link zu den Reports beim Lauf in GitHub Actions. Diesen Link der
Lehrperson geben.

### 2. Änderungen committen, Pull Request und Merge auf main

Die Änderungen liegen noch nicht in einem Commit. In kleinen Schritten committen, zum Beispiel:
1. Pipeline (projekt.yml, playwright.config.ts, .gitignore im Frontend)
2. Integrationstest von Google isolieren
3. Achievements auf Deutsch
4. Testkonzept
5. Reflexion
6. README, BESCHREIBUNG und Root-README

Am besten über einen eigenen Branch und einen Pull Request auf main. Der Merge löst die Pipeline mit Deploy aus.

### 3. Reflexion fertig schreiben

In [docs/reflexion.md](docs/reflexion.md) stehen die Fakten schon. Die Stellen in eckigen Klammern muss ich mit meiner
eigenen Erfahrung füllen.

### 4. KI-Nutzung fertig schreiben

In der [README](README.md#ki-nutzung) die Stellen mit «[ergänzen]» ausfüllen. Ehrlich bleiben und darauf achten, dass
nichts im Widerspruch zu anderen Dateien steht (z. B. FEHLENDE_TESTS.md).

### 5. Code Reviews durch eine Person

Verlangt sind pro Person 3 Pull Requests, die von jemand anderem kommentiert und challenged werden. Eine KI zählt
nicht. Für den Pull Request aus Punkt 2 eine Mitschülerin, einen Mitschüler oder die Lehrperson als Reviewer
hinzufügen, auf Kommentare antworten und Änderungen im selben Branch committen.

### 6. Präsentation

Leitfaden: [VIDEO-PRAESENTATION.md](VIDEO-PRAESENTATION.md).
