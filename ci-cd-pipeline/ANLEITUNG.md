# Anleitung CI/CD (für mich)

Was am 12.10. geändert wurde und wie ich es vorzeigen und erklären kann.

## Was sich geändert hat

1. **Doppelter Workflow gelöscht.** Unter `ci-cd-pipeline/.github/workflows/ci.yml` lag eine zweite Pipeline.
   GitHub liest Workflows aber nur aus `.github/workflows/` im Root des Repos. Die Datei im Unterordner lief also
   nie. Die README und der guide haben trotzdem diese Datei beschrieben. Das ist jetzt korrigiert.
2. **Reports auch bei roten Tests.** Vorher ist Maven bei einem fehlgeschlagenen Test sofort abgebrochen, und es
   gab keinen HTML-Report, also genau dann keinen, wenn man ihn am meisten braucht. Jetzt läuft:

   ```bash
   ./mvnw clean test -Dmaven.test.failure.ignore=true
   ```

   Danach werden die Reports hochgeladen, und erst ein letzter Schritt (`Fail on Test Failures`) schaut in den
   Surefire-XML-Dateien nach, ob es `<failure>` oder `<error>` gibt. Wenn ja, wird die Pipeline rot.

## Lokal ausführen

Mit Java 21:

```bash
cd ci-cd-pipeline/recipe-planner-fronend-and-backend/recipe-planner-backend
./mvnw clean test
```

Ohne Java, nur mit Docker:

```bash
cd ci-cd-pipeline/recipe-planner-fronend-and-backend/recipe-planner-backend
docker run --rm -u $(id -u):$(id -g) -e HOME=/tmp -v "$PWD":/app -w /app eclipse-temurin:21-jdk ./mvnw clean test
```

Danach im Browser öffnen:
- `target/site/jacoco/index.html` → Code Coverage
- `target/site/surefire-report.html` → Testresultate

## In der Pipeline zeigen

1. GitHub → Repo → Tab **Actions**
2. Den neusten Lauf von „Backend CI Pipeline“ anklicken
3. Unten bei **Artifacts** → `test-and-coverage-reports` herunterladen und entpacken
4. Darin `site/jacoco/index.html` und `site/surefire-report.html` öffnen

## Wenn die Lehrperson fragt

- *Warum Root-Ordner?* GitHub Actions sucht nur in `.github/workflows/` im Root.
- *Warum `failure.ignore`?* Damit es auch bei roten Tests einen Report gibt. Rot wird die Pipeline trotzdem,
  wegen des letzten Schritts.
- *Was bringen SoftAssertions?* Alle Assertions in einem Block werden geprüft, auch wenn eine davon fehlschlägt.
  Man sieht also alle Fehler auf einmal und nicht nur den ersten.
- *Wann läuft die Pipeline?* Bei jedem Push auf jeden Branch und bei jedem Pull Request.
