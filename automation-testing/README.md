# M450 Automation Testing – Gesamtdokumentation & Anleitung

Dieses Repository umfasst die vollständige, praxiserprobte Testautomatisierungs-Lösung für die Full-Stack-Applikation (Spring Boot Backend & Angular Frontend) im Rahmen des Moduls **M450 (Automation Testing)** an der TBZ.

---

## Inhaltsverzeichnis
1. [Status & Übersicht der Aufgaben](#1-status--übersicht-der-aufgaben)
2. [Systemarchitektur & Voraussetzungen](#2-systemarchitektur--voraussetzungen)
3. [Schritt-für-Schritt Schnellstart-Anleitung](#3-schritt-für-schritt-schnellstart-anleitung)
   - [3.1 Backend starten (Spring Boot)](#31-backend-starten-spring-boot)
   - [3.2 Frontend starten (Angular)](#32-frontend-starten-angular)
   - [3.3 H2-Datenbank-Konsole aufrufen](#33-h2-datenbank-konsole-aufrufen)
4. [Übung 1: REST-API Testautomatisierung (Backend)](#4-übung-1-rest-api-testautomatisierung-backend)
   - [4.1 MockMvc Integrationstests (JUnit 5)](#41-mockmvc-integrationstests-junit-5)
   - [4.2 Postman & Newman API Automation](#42-postman--newman-api-automation)
5. [Übung 2: End-to-End Frontend Testing (Playwright)](#5-übung-2-end-to-end-frontend-testing-playwright)
   - [5.1 Testumfang & E2E-Szenarien](#51-testumfang--e2e-szenarien)
   - [5.2 Ausführung & Headless-Modus](#52-ausführung--headless-modus)
6. [Übung 3: Last-, Performance- & Stresstests](#6-übung-3-last--performance---stresstests)
   - [6.1 Getestete Tools (ApacheBench, Autocannon, JMeter)](#61-getestete-tools-apachebench-autocannon-jmeter)
   - [6.2 Messwerte & Latenz-Analyse](#62-messwerte--latenz-analyse)
   - [6.3 Potenzielle Flaschenhälse (Bottlenecks)](#63-potenzielle-flaschenhälse-bottlenecks)
7. [Bonus-Feature: Kurszuordnung, Validierung & Error Handling](#7-bonus-feature-kurszuordnung-validierung--error-handling)
   - [7.1 Feature-Beschreibung](#71-feature-beschreibung)
   - [7.2 Zeitschätzung vs. Ist-Aufwand & Reflexion](#72-zeitschätzung-vs-ist-aufwand--reflexion)
8. [Troubleshooting & Häufige Fragen (FAQ)](#8-troubleshooting--häufige-fragen-faq)

---

## 1. Status & Übersicht der Aufgaben

Übersicht der Aufgaben und wo sie umgesetzt sind:

| Aufgabe / Modul | Status | Hauptbestandteile & Artefakte |
| :--- | :--- | :--- |
| **Lokales Setup** | **Vollständig** | Spring Boot 3.1.2 Backend (Port 8081), Angular 16 Frontend (Port 4200), H2 In-Memory DB |
| **Übung 1 (REST Tests)** | **Vollständig** | MockMvc Testsuite (`StudentControllerIntegrationTest.java`), Postman Collection (`Student_API.postman_collection.json`) |
| **Übung 2 (Frontend E2E)** | **Vollständig** | Playwright: `student-app.spec.ts` (4 Szenarien, Backend gemockt) und `full-stack.spec.ts` (echter Durchlauf Browser → Spring Boot → H2) |
| **Übung 3 (Load Testing)** | **Vollständig** | Last- und Stresstests mit ApacheBench (`ab`) & `autocannon` inkl. Latenz-Percentilen und Bottleneck-Analyse |
| **Bonusaufgabe** | **Vollständig** | Kurszuordnung (`course`), Jakarta Bean Validation (`@Valid`, `@Email`, `@NotBlank`), `@RestControllerAdvice`, Zeitschätzung & Reflexion |

---

## 2. Systemarchitektur & Voraussetzungen

### Technologiestack:
- **Backend:** Java 17, Spring Boot 3.1.2, Spring Data JPA, H2 Database, Jakarta Validation
- **Frontend:** Node.js (ab v18), Angular 16, TypeScript, Bootstrap 4, FontAwesome
- **Testing:** JUnit 5, MockMvc, AssertJ, Postman / Newman, Microsoft Playwright, ApacheBench, Autocannon

---

## 3. Schritt-für-Schritt Schnellstart-Anleitung

### 3.1 Backend starten (Spring Boot)
1. Terminal im Projektordner öffnen:
   ```bash
   cd spring-boot-angular-basic-lw2
   ```
2. Backend mit Maven starten:
   ```bash
   mvn spring-boot:run
   ```
3. Das Backend läuft unter: `http://localhost:8081`
4. REST-Endpunkt testen: `http://localhost:8081/students`

---

### 3.2 Frontend starten (Angular)
1. Neues Terminalfenster öffnen:
   ```bash
   cd spring-boot-angular-basic-lw2/src/main/js/my-app
   ```
2. Node-Pakete installieren (nur beim ersten Mal erforderlich):
   ```bash
   npm install
   ```
3. Angular Entwicklungsserver starten:
   ```bash
   npm start
   ```
4. Die Weboberfläche im Browser aufrufen: `http://localhost:4200`

---

### 3.3 H2-Datenbank-Konsole aufrufen
Die integrierte In-Memory-Datenbank kann direkt im Browser inspiziert werden:
- **URL:** `http://localhost:8081/h2-console`
- **JDBC URL:** `jdbc:h2:mem:testdb`
- **User Name:** `sa`
- **Password:** *(leer lassen)*

---

## 4. Übung 1: REST-API Testautomatisierung (Backend)

*Detaillierte Dokumentation:* [`UEBUNG_1_REST_AUTOMATION.md`](./UEBUNG_1_REST_AUTOMATION.md)

### 4.1 MockMvc Integrationstests (JUnit 5)
Die Integrationstests testen den Spring-Web-Layer ohne externen Webserver direkt im Speicher:
- **Speicherort:** `spring-boot-angular-basic-lw2/src/test/java/ch/tbz/m450/testing/tools/StudentControllerIntegrationTest.java`
- **Abgedeckte Testfälle:**
  1. `shouldReturnAllStudents`: Prüft `GET /students` (Status 200, JSON-Array, Datenfelder).
  2. `shouldCreateNewStudent`: Prüft `POST /students` (Status 201 Created, Speicherung in DB).
  3. `shouldRejectInvalidEmail`: Prüft Validation bei fehlerhafter E-Mail (Status 400 Bad Request).
  4. `shouldRejectEmptyName`: Prüft Validation bei leerem Namen (Status 400 Bad Request).
  5. `shouldAllowCorsFromAngularDevServer`: Verifiziert CORS-Header für `http://localhost:4200`.

**Ausführung via Maven:**
```bash
cd spring-boot-angular-basic-lw2
mvn test
```

---

### 4.2 Postman & Newman API Automation
- **Collection-Datei:** `testing/postman/Student_API.postman_collection.json`
- **Ausführung via Newman CLI:**
  ```bash
  npx newman run testing/postman/Student_API.postman_collection.json
  ```

---

## 5. Übung 2: End-to-End Frontend Testing (Playwright)

*Detaillierte Dokumentation:* [`UEBUNG_2_FRONTEND_E2E.md`](./UEBUNG_2_FRONTEND_E2E.md)

### 5.1 Testumfang & E2E-Szenarien
Mit **Microsoft Playwright** werden echte Interaktionen im Browser automatisiert getestet:
- **Speicherort:** `spring-boot-angular-basic-lw2/src/main/js/my-app/e2e/student-app.spec.ts`
- **Konfiguration:** `spring-boot-angular-basic-lw2/src/main/js/my-app/playwright.config.ts`

**Implementierte E2E-Szenarien:**
1. **Layout & Navigation:** Sichtbarkeit von Logo, Titel und Navigations-Buttons ("List Students", "Add Students").
2. **Tabellenanzeige & Mocking:** Abrufen und Rendern der Studentenliste mit Spalten ID, Name, E-Mail und Kurs.
3. **Formular & Erstellung:** Ausfüllen des Formulars, Dropdown-Auswahl des Kurses, Absenden und Weiterleitung.
4. **Client-Validierung:** Deaktivierter Submit-Button solange Pflichtfelder fehlen oder ungültig sind.

Die Szenarien 2 und 3 ersetzen das Backend mit `page.route()` durch Testdaten. Dadurch laufen sie isoliert und
stabil, testen aber nicht den ganzen Weg. Deshalb gibt es zusätzlich `e2e/full-stack.spec.ts`: Er legt über das
GUI einen Studenten an, gegen das **echte** Spring-Boot-Backend, und prüft das Ergebnis im GUI und über die
REST-API. Läuft das Backend nicht, wird dieser Test mit einem Hinweis übersprungen.

### 5.2 Ausführung
```bash
# Terminal 1 (nur für full-stack.spec.ts nötig): Backend starten
cd spring-boot-angular-basic-lw2
./mvnw spring-boot:run

# Terminal 2: E2E-Tests (startet das Angular-Frontend automatisch)
cd spring-boot-angular-basic-lw2/src/main/js/my-app
npm run test:e2e
npx playwright show-report   # HTML-Report ansehen
```

---

## 6. Übung 3: Last-, Performance- & Stresstests

*Detaillierte Dokumentation:* [`UEBUNG_3_LOAD_TESTING.md`](./UEBUNG_3_LOAD_TESTING.md)

### 6.1 Getestete Tools
- **ApacheBench (`ab`):** Schnelle Baseline-Messungen mit hohem Concurrency-Grad.
- **Autocannon:** HTTP/1.1 Benchmarking mit genauer Perzentil-Latenzmessung.
- **Postman Runner & JMeter:** Vergleich und Einordnung für komplexe Testpläne.

### 6.2 Messwerte & Latenz-Analyse

#### Test A: `GET /students` (Lesezugriff)
- **ApacheBench (1'000 Requests, Concurrency 50):**
  - Durchsatz: **22'859 Req/s**
  - Mittlere Latenz: **2.187 ms**
  - Fehlerrate: **0%**
- **Autocannon (10 Verbindungen, 5 Sekunden):**
  - Gesamtanzahl: **219'000 Requests**
  - Durchsatz: **43'440 Req/s**
  - Median Latenz (p50): **23 ms**, p99: **47 ms**

#### Test B: `POST /students` (Schreibzugriff)
- **Autocannon (20 Verbindungen, 5 Sekunden, JSON Payload):**
  - Gesamtanzahl: **287'000 Requests**
  - Durchsatz: **56'739 Req/s**
  - Median Latenz (p50): **24 ms**, p99: **62 ms**, Max: **78 ms**

#### Nachmessung mit Rohdaten (WSL2/Docker)
Die Werte oben stammen aus der ersten Messung (ohne Rohdaten). Die Nachmessung mit gespeicherter Tool-Ausgabe
([`testing/load-results/`](testing/load-results)) ergab GET etwa **10'000 req/s** und POST etwa **12'900 req/s**
(autocannon), jeweils ohne Fehler. Nach dem POST-Test (64'660 neue Datensätze) fällt `GET /students` auf
**28 req/s**, weil die Liste nicht paginiert ist. Details: [UEBUNG_3_LOAD_TESTING.md, Abschnitt 4](./UEBUNG_3_LOAD_TESTING.md#4-nachmessung-mit-rohdaten-12102026).

### 6.3 Potenzielle Flaschenhälse (Bottlenecks)
1. **Fehlende Paginierung bei `GET /students`:** (in der Nachmessung gemessen: 4,8 MB pro Antwort, 28 req/s)
2. **HikariCP Connection Pool:** Standardmässig auf 10 Verbindungen limitiert.
3. **Tomcat Worker Threads:** Standardmässig 200 blockierende Worker-Threads.
4. **Garbage Collection (GC):** Hohe Objektallokation bei zehntausenden JSON-DTOs pro Sekunde.

---

## 7. Bonus-Feature: Kurszuordnung, Validierung & Error Handling

*Detaillierte Dokumentation:* [`BONUS_FEATURE.md`](./BONUS_FEATURE.md)

### 7.1 Feature-Beschreibung
1. **Neues Feld `course`:** Erweiterung der Entität `Student` und des Angular-Modells inklusive Dropdown-Auswahl ("Informatik", "Mediamatik", "Elektronik", "Wirtschaftsinformatik").
2. **Strikte Backend-Validierung:** Einsatz von Jakarta Validation (`@NotBlank`, `@Email`) auf REST-Ebene.
3. **Global Exception Handling:** `@RestControllerAdvice` fängt Validierungsfehler ab und liefert saubere HTTP 400 Fehlerstrukturen.
4. **Verbesserte UI-Validierung:** Reaktives Feedback und Submit-Sperre bei Fehleingaben.

### 7.2 Zeitschätzung vs. Ist-Aufwand & Reflexion
- **Geplante Zeit (Soll):** 45 Minuten (1 Schullektion)
- **Tatsächliche Zeit (Ist):** 35 Minuten
- **Erkenntnis:** Das Zusammenspiel aus Backend-Validierung und automatisierter Testsuite (MockMvc + Playwright) ermöglichte blitzschnelle Feedbackschleifen und verhinderte zeitraubendes manuelles Testen.

---

## 8. Troubleshooting & Häufige Fragen (FAQ)

### Backend startet nicht (Port 8081 belegt):
Prüfen Sie, ob ein alter Prozess läuft:
```bash
lsof -i :8081
kill -9 <PID>
```

### Frontend kann Backend nicht erreichen (CORS / Connection Refused):
- Stellen Sie sicher, dass das Backend auf Port 8081 läuft.
- Überprüfen Sie die `@CrossOrigin(origins = "http://localhost:4200")` Annotation am `StudentController`.

### Playwright Browser fehlen:
Installieren Sie die Browser-Binaries mit:
```bash
cd spring-boot-angular-basic-lw2/src/main/js/my-app
npx playwright install
```

---

## Nachtrag 12.10.2026

- **Echter E2E-Test:** Zu den bestehenden Playwright-Tests (Backend gemockt) kommt `e2e/full-stack.spec.ts` dazu.
  Er geht wirklich durch Browser → Angular → Spring Boot → H2. Alle 5 Tests laufen grün.
- **Maven Wrapper repariert:** `./mvnw` wollte `.mvn/wrapper/maven-wrapper.properties` lesen, und die Datei war nie
  im Repo. Jetzt funktioniert das Starten mit `./mvnw spring-boot:run` auch nach einem frischen Clone.
- **Lasttest nachgemessen**, diesmal mit gespeicherter Tool-Ausgabe (`testing/load-results/`). Dabei ist mir
  aufgefallen, dass `GET /students` keine Paginierung hat: Nach dem POST-Test mit über 64'000 neuen Einträgen
  schafft der Endpunkt nur noch 28 statt etwa 10'000 Requests pro Sekunde.
- Generierte Playwright-Reports sind nicht mehr im Repo (`.gitignore`).

**KI-Nutzung:** Diese Nachträge wurden mit Claude Code (KI-Agent) erarbeitet. Ich habe die Tests und Messungen
selbst nachvollzogen.

---
*TBZ – Modul M450: Automation Testing*
