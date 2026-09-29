# Recipe Planner - CI/CD Pipeline & Testing Dokumentation

Dieses Repository beinhaltet die vollstaendige Loesung fuer die geforderten Aufgaben zu Unit Testing, automatisierten Reports und der CI/CD-Pipeline im Modul M450.

---

## Inhaltsverzeichnis
1. [Status der Aufgaben](#status-der-aufgaben)
2. [Aufgabe 1: Unit Testing](#aufgabe-1-unit-testing)
   - [Controller Testing (MockMvc)](#controller-testing-mockmvc)
   - [Mapper Testing & SoftAssertions](#mapper-testing--softassertions)
   - [Vorteile von SoftAssertions](#vorteile-von-softassertions)
3. [Aufgabe 2: Test- und Abdeckungs-Reports](#aufgabe-2-test--und-abdeckungs-reports)
   - [JaCoCo (Code Coverage)](#jacoco-code-coverage)
   - [Surefire (Test Reports)](#surefire-test-reports)
   - [Reports lokal einsehen](#reports-lokal-einsehen)
4. [Aufgabe 3: CI/CD Pipeline (GitHub Actions)](#aufgabe-3-cicd-pipeline-github-actions)
   - [Workflow-Uebersicht](#workflow-uebersicht)
   - [Artefakte & Reports in der Pipeline](#artefakte--reports-in-der-pipeline)
5. [Anleitung zur Inbetriebnahme](#anleitung-zur-inbetriebnahme)
   - [Voraussetzungen](#voraussetzungen)
   - [Backend starten & testen](#backend-starten--testen)
   - [Frontend starten](#frontend-starten)

---

## Status der Aufgaben

Alle Aufgaben (1, 2 und 3) wurden **vollstaendig und erfolgreich umgesetzt**:

- [x] **Aufgabe 1 - Unit Testing**:
  - Alle Controller-Methoden via `MockMvc` vollstaendig getestet.
  - Mapper-Klassen (`IngredientEntityMapper`, `RecipeEntityMapper`) mit `SoftAssertions` getestet.
  - Verstaendnis und Erklaerung von `SoftAssertions` dokumentiert.
- [x] **Aufgabe 2 - Reports**:
  - JaCoCo Coverage Report generiert HTML-Reports automatisch.
  - Maven Surefire Report generiert ausfuehrliche Testberichte.
- [x] **Aufgabe 3 - Pipeline**:
  - GitHub Actions Workflow (`.github/workflows/ci.yml`) aufgesetzt.
  - Pipeline triggert automatisch bei `push` und `pull_request`.
  - Ausfuehrung saemtlicher Unit Tests in der Cloud.
  - Reports werden als Pipeline-Artefakte gespeichert und sind direkt downloadbar.

---

## Aufgabe 1: Unit Testing

### Controller Testing (MockMvc)
Die Tests fuer `RecipeController` befinden sich in `src/test/java/ch/tbz/recipe/planner/controller/RecipeControllerTest.java`.  
Getestet werden saemtliche Endpunkte ueber `MockMvc`:

1. `GET /api/recipes`:
   - Prueft das Abrufen aller Rezepte (Status `200 OK`, JSON-Array-Rueckgabe).
2. `GET /api/recipes/recipe/{id}`:
   - Prueft das Abrufen eines spezifischen Rezepts anhand der UUID (Status `200 OK`, Rueckgabe des passenden Objekts).
3. `POST /api/recipes`:
   - Prueft das Erstellen eines neuen Rezepts (Status `200 OK`, korrekte Deserialisierung und Persistierung).

### Mapper Testing & SoftAssertions
Die Mapper-Klassen wandeln zwischen den Entitaeten (`IngredientEntity`, `RecipeEntity`) und den Domaenenobjekten (`Ingredient`, `Recipe`) um.
Fuer beide Mapper wurden dedizierte Testklassen erstellt:
- `IngredientEntityMapperTest.java`: Testet `entityToDomain`, `domainToEntity`, `entitiesToDomains` und `domainsToEntities`.
- `RecipeEntityMapperTest.java`: Testet `entityToDomain` und `domainToEntity` inklusive der verschachtelten Zutatenliste.

### Vorteile von SoftAssertions
Bei herkoemmlichen Assertions (`assertThat(...)` oder `assertEquals(...)`) bricht der gesamte Testfall sofort ab, sobald die **erste** Bedingung fehlschlaegt. Nachfolgende Felder werden gar nicht mehr geprueft.

**Vorteile von `SoftAssertions` (AssertJ):**
1. **Vollstaendige Fehleranalyse:** Alle Assertions innerhalb eines Blocks werden ausgefuehrt, auch wenn fruehere Pruefungen fehlschlagen.
2. **Umfassender Ueberblick bei komplexen Objekten:** Bei Mappern mit vielen Feldern sieht man auf einen Blick *alle* fehlerhaft gemappten Attribute in einem einzigen Testdurchlauf.
3. **Zeitersparnis beim Debugging:** Entwickler muessen den Test nicht mehrfach fuer jedes einzelne Attribut iterativ reparieren.

Verwendung im Code:
```java
SoftAssertions.assertSoftly(softly -> {
    softly.assertThat(domain.getId()).isEqualTo(entity.getId());
    softly.assertThat(domain.getName()).isEqualTo(entity.getName());
    softly.assertThat(domain.getComment()).isEqualTo(entity.getComment());
    softly.assertThat(domain.getUnit()).isEqualTo(entity.getUnit());
    softly.assertThat(domain.getAmount()).isEqualTo(entity.getAmount());
});
```

---

## Aufgabe 2: Test- und Abdeckungs-Reports

In der `pom.xml` des Backends wurden die Plugins zur automatischen Berichterstellung konfiguriert:

### JaCoCo (Code Coverage)
- **Plugin:** `jacoco-maven-plugin` (Version 0.8.11)
- **Funktion:** Misst waehrend der Testausfuehrung die Codeabdeckung (Zeilen-, Verzweigungs- und Methodenabdeckung).
- **Report-Pfad:** `recipe-planner-backend/target/site/jacoco/index.html`

### Surefire (Test Reports)
- **Plugin:** `maven-surefire-report-plugin` (Version 3.2.3)
- **Funktion:** Generiert eine uebersichtliche HTML-Zusammenfassung aller ausgefuehrten Tests, Durchlaufzeiten und Erfolgsraten.
- **Report-Pfad:** `recipe-planner-backend/target/site/surefire-report.html`

### Reports lokal einsehen
Fuer die Generierung der Reports genuegt der normale Testbefehl:
```bash
cd recipe-planner-fronend-and-backend/recipe-planner-backend
./mvnw clean test
```
Anschliessend koennen die HTML-Dateien im Browser geoeffnet werden:
```bash
# macOS
open target/site/jacoco/index.html
open target/site/surefire-report.html

# Linux
xdg-open target/site/jacoco/index.html
```

---

## Aufgabe 3: CI/CD Pipeline (GitHub Actions)

Die Pipeline ist unter `.github/workflows/ci.yml` im Repository-Root definiert.

### Workflow-Uebersicht
- **Trigger:**
  - Jeder `git push` auf den Branch `main`
  - Jeder `pull_request` gegen den Branch `main`
- **Runner-Umgebung:** `ubuntu-latest`
- **Java-Version:** OpenJDK 21 (Temurin)

### Pipeline-Schritte
1. **Checkout Code:** Laedt das gesamte Repository herunter.
2. **Set up JDK 21:** Richtet Java 21 und den Maven-Cache ein.
3. **Run Unit Tests & Generate Reports:**  
   Fuehrt `./mvnw clean test` im Ordner `recipe-planner-backend` aus.
4. **Upload JaCoCo Coverage Report:**  
   Laedt `target/site/jacoco/` als Workflow-Artefakt `jacoco-coverage-report` hoch.
5. **Upload Surefire Test Report:**  
   Laedt `target/site/` & `target/surefire-reports/` als Artefakt `surefire-test-report` hoch.

### Artefakte & Reports in der Pipeline
Nach jedem Durchlauf stehen die Reports direkt im GitHub Actions Tab unter **Artifacts** zum Download bereit.

---

## Anleitung zur Inbetriebnahme

### Voraussetzungen
- **Java:** JDK 21 oder neuer
- **Node.js & npm:** (Fuer das Frontend)
- **Git**

### Backend starten & testen

1. In das Backend-Verzeichnis navigieren:
   ```bash
   cd recipe-planner-fronend-and-backend/recipe-planner-backend
   ```

2. Tests ausfuehren und Reports generieren:
   ```bash
   ./mvnw clean test
   ```

3. Backend-Applikation starten:
   ```bash
   ./mvnw spring-boot:run
   ```
   *Das Backend laeuft standardmaessig unter `http://localhost:8080`.*

### Frontend starten

1. In das Frontend-Verzeichnis navigieren:
   ```bash
   cd recipe-planner-fronend-and-backend/recipe-planner-fronend
   ```

2. Abhaengigkeiten installieren:
   ```bash
   npm install
   ```

3. Frontend Entwicklungsserver starten:
   ```bash
   npm start
   ```
   *Die React-Anwendung oeffnet sich anschliessend unter `http://localhost:3000`.*
