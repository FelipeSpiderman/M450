# Übung 1: Automatisierte REST-Schnittstellen-Tests (Backend)

## 1. Zielsetzung
Automatisiertes Testen der REST-Schnittstellen (`/students`) der Spring Boot Backend-Applikation. Das Ziel ist es, sicherzustellen, dass die HTTP-Endpunkte korrekt funktionieren, die JSON-Strukturen validieren und Fehlerfälle (z. B. Validierungsfehler) sauber behandelt werden.

---

## 2. Ausgewählte Tools & Ansätze

Für die Testautomatisierung der REST-API wurden zwei komplementäre Ansätze implementiert:

1. **Spring Boot MockMvc / JUnit 5 (Code-basierte Integrationstests)**
   - **Vorteile:**
     - Direkte Integration in die Maven-Build-Pipeline (`mvn test`).
     - Kein extern laufender Server erforderlich (schnelle In-Memory-Ausführung).
     - Typsichere Assertions über Fluent-API (`status().isOk()`, `jsonPath(...)`).
     - Vollständige Überprüfung von HTTP-Statuscodes, Responsekörpern, Headern und Datenbankzuständen.
   - **Ort:** `spring-boot-angular-basic-lw2/src/test/java/ch/tbz/m450/testing/tools/StudentControllerIntegrationTest.java`

2. **Postman / Newman Test Suite (API Client Automation)**
   - **Vorteile:**
     - Grafische Benutzeroberfläche zur schnellen interaktiven Erkundung.
     - Automatisierte JavaScript-Tests (`pm.test()`, `pm.response.to.have.status(200)`).
     - Headless ausführbar über CLI mit Newman in CI/CD Pipelines.
   - **Ort:** `testing/postman/Student_API.postman_collection.json`

---

## 3. Implementierte Test-Szenarien

| Test Case | Methode | Endpunkt | Erwarteter Status | Prüfungen |
|-----------|---------|----------|-------------------|-----------|
| `shouldReturnAllStudents` | `GET` | `/students` | `200 OK` | Gibt JSON-Liste zurück, verifiziert Felder `id`, `name`, `email` |
| `shouldCreateNewStudent` | `POST` | `/students` | `201 Created` | Speichert Student in H2-DB, verifiziert generierte ID und Persistenz |
| `shouldRejectInvalidEmail` | `POST` | `/students` | `400 Bad Request` | Verifiziert Fehlermeldung bei ungültiger E-Mail-Adresse (`@Email`) |
| `shouldRejectEmptyName` | `POST` | `/students` | `400 Bad Request` | Verifiziert Fehlermeldung bei leerem Namen (`@NotBlank`) |
| `shouldAllowCorsFromAngularDevServer` | `GET` | `/students` | `200 OK` | Verifiziert `Access-Control-Allow-Origin: http://localhost:4200` Header |

---

## 4. Ausführung der automatisierten Tests

### Ausführung via Maven:
```bash
cd spring-boot-angular-basic-lw2
mvn test
```

### Testergebnis:
```text
[INFO] Running ch.tbz.m450.testing.tools.StudentControllerIntegrationTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Ausführung via Postman / Newman:
Importieren Sie `testing/postman/Student_API.postman_collection.json` in Postman und starten Sie den "Collection Runner" oder über Newman:
```bash
npx newman run testing/postman/Student_API.postman_collection.json
```
