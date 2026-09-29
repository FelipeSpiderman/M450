# Bonus Aufgabe: Feature-Definition, Zeitschätzung & Reflexion

## 1. Feature-Definition

### Feature-Name:
**Erweitertes Studenten-Modell mit Kurszuordnung, strikter Input-Validierung und standardisiertem Global Error Handling**

### Detaillierte Beschreibung:
1. **Zusätzliches Datenfeld `course`:**
   - Einbindung des Feldes `course` (z. B. "Informatik", "Mediamatik", "Elektronik", "Wirtschaftsinformatik") im Backend-Entity `Student` und im Angular-Model.
   - Frontend: Auswahlmöglichkeit über ein `<select>`-Dropdown im Registrierungsformular sowie Anzeige als Badge in der Studentenübersicht.
2. **Backend Input-Validierung:**
   - Integration von Jakarta Bean Validation (`spring-boot-starter-validation`).
   - `@NotBlank(message = "Name is required")` auf dem Namensfeld.
   - `@NotBlank` und `@Email(message = "Email must be a valid email address")` auf dem E-Mail-Feld.
   - Aktivierung mit `@Valid` auf dem `@PostMapping("/students")` Endpunkt.
3. **Globales Exception Handling (`@RestControllerAdvice`):**
   - Abfangen von `MethodArgumentNotValidException` und Rückgabe strukturierter Fehlermeldungen mit HTTP 400 Bad Request und Feld-Detailinformationen (`timestamp`, `status`, `error`, `errors: { field: message }`).
4. **Frontend Formularvalidierung:**
   - Sofortige visuelle Fehleranzeige im Angular-Formular bei ungültigen Eingaben (`touched`/`dirty`).
   - Deaktivierung des Submit-Buttons solange das Formular ungültig ist.

---

## 2. Zeitschätzung (Planung: 1 Lektion / 45 Minuten)

| Arbeitsschritt | Geplante Dauer |
|----------------|----------------|
| 1. Backend-Erweiterung (`Student.java`, `pom.xml` Dependencies, `@Valid`) | 12 Min. |
| 2. Implementierung `GlobalExceptionHandler` mit strukturierter JSON-Antwort | 8 Min. |
| 3. Frontend-Anpassungen (`student.ts`, Formular mit Dropdown & Validierung, Liste) | 15 Min. |
| 4. Test-Aktualisierung (MockMvc REST Tests & Playwright E2E Tests) | 10 Min. |
| **Gesamtschätzung:** | **45 Min.** |

---

## 3. Tatsächlicher Zeitaufwand (Ist-Zeit)

| Arbeitsschritt | Tatsächliche Dauer |
|----------------|--------------------|
| 1. Backend-Erweiterung & Validation-Starter | 10 Min. |
| 2. `GlobalExceptionHandler` & Error-DTO | 6 Min. |
| 3. Frontend-Modell, HTML-Templates & CSS | 12 Min. |
| 4. Integrationstests & Playwright E2E Tests anpassen | 7 Min. |
| **Gesamtaufwand:** | **35 Min.** |

---

## 4. Reflexion

- **Was lief gut?**
  - Die modulare Struktur von Spring Boot erlaubte das schnelle Einbinden von `@Valid` und `@RestControllerAdvice` ohne tiefgreifende Änderungen an der bestehenden Architektur.
  - Das Zusammenspiel zwischen Angular Formularvalidierung und Backend-Validierung bietet vollständige Sicherheit (Defense in Depth: UI-Feedback für den Benutzer + REST-Validierung gegen fehlerhafte API-Clients).
- **Wo gab es Überraschungen?**
  - Die bestehende Vorlage in Angular hatte die Validierungslogik mit `[hidden]="!name.pristine"` unpassend formuliert (Fehlermeldung erschien bei leerem Formular sofort). Dies wurde auf sauberes `name.invalid && (name.dirty || name.touched)` umgestellt.
- **Mehrwert automatisierter Tests:**
  - Dank der zuvor aufgesetzten MockMvc- und Playwright-Tests konnte die Funktionsfähigkeit des neuen Features in wenigen Sekunden automatisiert verifiziert werden, anstatt mühsam manuell Daten über das GUI einzugeben.
