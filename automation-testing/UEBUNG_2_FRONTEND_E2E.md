# Übung 2: End-to-End Frontend Testing (Angular GUI)

## 1. Tool-Auswahl: Playwright

Für das automatisierte Testen der Angular-Benutzeroberfläche im Browser wurde **Playwright** (Microsoft) ausgewählt.

### Warum Playwright?
1. **Headless & Headed Browser Support:** Unterstützt native Browser-Engines (Chromium, Firefox, WebKit) und lässt sich sowohl lokal als auch in CI/CD ohne Display-Server ausführen.
2. **Automatisches Warten (Auto-Waiting):** Playwright wartet automatisch, bis Elemente im DOM sichtbar, interaktiv und klickbar sind, was typische "Flaky Tests" verhindert.
3. **Netzwerk-Mocking & Interception:** Ermöglicht das Abfangen von Backend-Anfragen (`page.route()`), sodass Frontend-Tests isoliert, reproduzierbar und unabhängig von Datenbankzuständen laufen können.
4. **Dev-Server Integration:** Startet die Angular-Applikation (`ng serve`) bei Bedarf automatisch hoch (`webServer` in `playwright.config.ts`).

---

## 2. Test-Konfiguration & Struktur

- **Konfiguration:** `spring-boot-angular-basic-lw2/src/main/js/my-app/playwright.config.ts`
- **Test-Spezifikation:** `spring-boot-angular-basic-lw2/src/main/js/my-app/e2e/student-app.spec.ts`

### Implementierte Testfälle:

1. **`should display home layout with navigation buttons and logo`**
   - Prüft, ob das TBZ-Logo und die Navigationsschaltflächen ("List Students", "Add Students") geladen und sichtbar sind.

2. **`should navigate to /students and render student list table with course column`**
   - Simuliert Klick auf "/students", interceptet REST GET-Aufruf und verifiziert das Tabellen-Rendering samt Spalten (ID, Name, Email, Course).

3. **`should navigate to /addstudents, select course, and add a new student successfully`**
   - Navigiert zum Formular `/addstudents`, füllt Textfelder für Name und E-Mail aus, wählt einen Kurs aus ("Mediamatik"), klickt auf "Submit", interceptet POST-Request und verifiziert die Weiterleitung zur Liste.

4. **`should validate form and disable submit when required fields are missing`**
   - Prüft die clientseitige Angular-Formularvalidierung: Der Submit-Button bleibt deaktiviert (`disabled`), solange Pflichtfelder fehlen oder ungültig sind.

---

## 3. Ausführung der E2E-Tests

Im Verzeichnis `spring-boot-angular-basic-lw2/src/main/js/my-app`:

```bash
npm run test:e2e
```

### Ausgabe der Testausführung:
```text
Running 4 tests using 1 worker

  ✓  1 [chromium] › e2e/student-app.spec.ts:5:7 › Student Application End-to-End Tests › should display home layout with navigation buttons and logo (270ms)
  ✓  2 [chromium] › e2e/student-app.spec.ts:19:7 › Student Application End-to-End Tests › should navigate to /students and render student list table with course column (189ms)
  ✓  3 [chromium] › e2e/student-app.spec.ts:47:7 › Student Application End-to-End Tests › should navigate to /addstudents, select course, and add a new student successfully (254ms)
  ✓  4 [chromium] › e2e/student-app.spec.ts:87:7 › Student Application End-to-End Tests › should validate form and disable submit when required fields are missing (154ms)

  4 passed (4.2s)
```
