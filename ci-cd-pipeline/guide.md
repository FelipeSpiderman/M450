# Schritt-für-Schritt Anleitung: CI/CD Pipeline & Unit Testing (M450)

Diese Anleitung führt Sie Schritt für Schritt durch die vollständige Umsetzung der drei Aufgaben des Moduls M450 (Unit Testing, Reports & CI/CD Pipeline) für das **Recipe Planner** Projekt.

---

## Inhaltsübersicht

1. [Projektüberblick & Vorbereitung](#1-projektüberblick--vorbereitung)
2. [Aufgabe 1: Unit Testing](#2-aufgabe-1-unit-testing)
   - [2.1 Controller Tests mit MockMvc](#21-controller-tests-mit-mockmvc)
   - [2.2 Mapper Tests mit SoftAssertions](#22-mapper-tests-mit-softassertions)
   - [2.3 Erklärung: Warum SoftAssertions?](#23-erklärung-warum-softassertions)
3. [Aufgabe 2: Test- & Abdeckungs-Reports](#3-aufgabe-2-test---abdeckungs-reports)
   - [3.1 JaCoCo Code Coverage konfigurieren](#31-jacoco-code-coverage-konfigurieren)
   - [3.2 Surefire Test Reports konfigurieren](#32-surefire-test-reports-konfigurieren)
   - [3.3 Reports lokal generieren und einsehen](#33-reports-lokal-generieren-und-einsehen)
4. [Aufgabe 3: CI/CD Pipeline](#4-aufgabe-3-cicd-pipeline)
   - [4.1 GitHub Actions Workflow erstellen](#41-github-actions-workflow-erstellen)
   - [4.2 GitLab CI Alternative (optional)](#42-gitlab-ci-alternative-optional)
   - [4.3 Pipeline testen & Artefakte prüfen](#43-pipeline-testen--artefakte-prüfen)
5. [Inbetriebnahme & Präsentations-Checkliste](#5-inbetriebnahme--präsentations-checkliste)

---

## 1. Projektüberblick & Vorbereitung

Die Projektstruktur gliedert sich wie folgt:
- `recipe-planner-fronend-and-backend/recipe-planner-backend`: Spring Boot (Java 21) Backend mit REST-Schnittstellen, H2-Datenbank und MapStruct.
- `recipe-planner-fronend-and-backend/recipe-planner-fronend`: React Frontend.
- `.github/workflows/ci.yml`: GitHub Actions Workflow für automatisierte Tests und Berichte.

> **Wichtig:** Sämtliche Änderungen für diese Aufgabenstellung finden im **Backend** und in den Pipeline-Konfigurationen statt.

---

## 2. Aufgabe 1: Unit Testing

### 2.1 Controller Tests mit MockMvc

Der `RecipeController` stellt drei Endpunkte zur Verfügung:
1. `GET /api/recipes` – Alle Rezepte abrufen.
2. `GET /api/recipes/recipe/{id}` – Einzelnes Rezept nach ID abrufen.
3. `POST /api/recipes` – Neues Rezept erstellen.

Erstellen oder öffnen Sie die Datei `src/test/java/ch/tbz/recipe/planner/controller/RecipeControllerTest.java` im Backend:

```java
package ch.tbz.recipe.planner.controller;

import ch.tbz.recipe.planner.domain.Recipe;
import ch.tbz.recipe.planner.service.RecipeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RecipeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RecipeService recipeService;

    @InjectMocks
    private RecipeController recipeController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(recipeController).build();
    }

    @Test
    void getRecipes_shouldReturnListOfRecipes() throws Exception {
        UUID id = UUID.randomUUID();
        Recipe recipe = new Recipe();
        recipe.setId(id);
        recipe.setName("Spaghetti Carbonara");
        recipe.setDescription("Klassische Carbonara");

        when(recipeService.getRecipes()).thenReturn(List.of(recipe));

        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(id.toString()))
                .andExpect(jsonPath("$[0].name").value("Spaghetti Carbonara"))
                .andExpect(jsonPath("$[0].description").value("Klassische Carbonara"));
    }

    @Test
    void getRecipe_shouldReturnSingleRecipe() throws Exception {
        UUID id = UUID.randomUUID();
        Recipe recipe = new Recipe();
        recipe.setId(id);
        recipe.setName("Pizza Margherita");

        when(recipeService.getRecipe(id)).thenReturn(recipe);

        mockMvc.perform(get("/api/recipes/recipe/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Pizza Margherita"));
    }

    @Test
    void addRecipe_shouldCreateAndReturnRecipe() throws Exception {
        UUID id = UUID.randomUUID();
        Recipe inputRecipe = new Recipe();
        inputRecipe.setName("Risotto");
        inputRecipe.setDescription("Pilzrisotto");

        Recipe savedRecipe = new Recipe();
        savedRecipe.setId(id);
        savedRecipe.setName("Risotto");
        savedRecipe.setDescription("Pilzrisotto");

        when(recipeService.addRecipe(any(Recipe.class))).thenReturn(savedRecipe);

        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputRecipe)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Risotto"))
                .andExpect(jsonPath("$.description").value("Pilzrisotto"));
    }
}
```

---

### 2.2 Mapper Tests mit SoftAssertions

Für das Mapping existieren zwei Mapper: `IngredientEntityMapper` und `RecipeEntityMapper`.

#### 1. `IngredientEntityMapperTest.java`
Erstellen Sie `src/test/java/ch/tbz/recipe/planner/mapper/IngredientEntityMapperTest.java`:

```java
package ch.tbz.recipe.planner.mapper;

import ch.tbz.recipe.planner.domain.Ingredient;
import ch.tbz.recipe.planner.entities.IngredientEntity;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

public class IngredientEntityMapperTest {

    private final IngredientEntityMapper mapper = Mappers.getMapper(IngredientEntityMapper.class);

    @Test
    void entityToDomain_mapsAllFieldsCorrectly() {
        IngredientEntity entity = new IngredientEntity();
        entity.setId(UUID.randomUUID());
        entity.setName("Zucker");
        entity.setComment("Fein gemahlen");
        entity.setUnit("g");
        entity.setAmount(200.0);

        Ingredient domain = mapper.entityToDomain(entity);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(domain).isNotNull();
            softly.assertThat(domain.getId()).isEqualTo(entity.getId());
            softly.assertThat(domain.getName()).isEqualTo(entity.getName());
            softly.assertThat(domain.getComment()).isEqualTo(entity.getComment());
            softly.assertThat(domain.getUnit()).isEqualTo(entity.getUnit());
            softly.assertThat(domain.getAmount()).isEqualTo(entity.getAmount());
        });
    }

    @Test
    void domainToEntity_mapsAllFieldsCorrectly() {
        Ingredient domain = new Ingredient();
        domain.setId(UUID.randomUUID());
        domain.setName("Mehl");
        domain.setComment("Typ 405");
        domain.setUnit("kg");
        domain.setAmount(1.5);

        IngredientEntity entity = mapper.domainToEntity(domain);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(entity).isNotNull();
            softly.assertThat(entity.getId()).isEqualTo(domain.getId());
            softly.assertThat(entity.getName()).isEqualTo(domain.getName());
            softly.assertThat(entity.getComment()).isEqualTo(domain.getComment());
            softly.assertThat(entity.getUnit()).isEqualTo(domain.getUnit());
            softly.assertThat(entity.getAmount()).isEqualTo(domain.getAmount());
        });
    }

    @Test
    void entitiesToDomains_mapsListCorrectly() {
        IngredientEntity entity = new IngredientEntity();
        entity.setId(UUID.randomUUID());
        entity.setName("Salz");

        List<Ingredient> domains = mapper.entitiesToDomains(List.of(entity));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(domains).isNotNull().hasSize(1);
            softly.assertThat(domains.get(0).getId()).isEqualTo(entity.getId());
            softly.assertThat(domains.get(0).getName()).isEqualTo(entity.getName());
        });
    }

    @Test
    void domainsToEntities_mapsListCorrectly() {
        Ingredient domain = new Ingredient();
        domain.setId(UUID.randomUUID());
        domain.setName("Pfeffer");

        List<IngredientEntity> entities = mapper.domainsToEntities(List.of(domain));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(entities).isNotNull().hasSize(1);
            softly.assertThat(entities.get(0).getId()).isEqualTo(domain.getId());
            softly.assertThat(entities.get(0).getName()).isEqualTo(domain.getName());
        });
    }
}
```

#### 2. `RecipeEntityMapperTest.java`
Erstellen Sie `src/test/java/ch/tbz/recipe/planner/mapper/RecipeEntityMapperTest.java`:

```java
package ch.tbz.recipe.planner.mapper;

import ch.tbz.recipe.planner.domain.Recipe;
import ch.tbz.recipe.planner.entities.IngredientEntity;
import ch.tbz.recipe.planner.entities.RecipeEntity;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

public class RecipeEntityMapperTest {

    private final RecipeEntityMapper mapper = Mappers.getMapper(RecipeEntityMapper.class);

    @Test
    void entityToDomain_mapsRecipeAndNestedIngredients() {
        IngredientEntity ingredientEntity = new IngredientEntity();
        ingredientEntity.setId(UUID.randomUUID());
        ingredientEntity.setName("Tomatensauce");

        RecipeEntity recipeEntity = new RecipeEntity();
        recipeEntity.setId(UUID.randomUUID());
        recipeEntity.setName("Pasta");
        recipeEntity.setDescription("Einfache Pasta");
        recipeEntity.setImageUrl("https://example.com/pasta.jpg");
        recipeEntity.setIngredients(List.of(ingredientEntity));

        Recipe domain = mapper.entityToDomain(recipeEntity);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(domain).isNotNull();
            softly.assertThat(domain.getId()).isEqualTo(recipeEntity.getId());
            softly.assertThat(domain.getName()).isEqualTo(recipeEntity.getName());
            softly.assertThat(domain.getDescription()).isEqualTo(recipeEntity.getDescription());
            softly.assertThat(domain.getImageUrl()).isEqualTo(recipeEntity.getImageUrl());
            softly.assertThat(domain.getIngredients()).hasSize(1);
            softly.assertThat(domain.getIngredients().get(0).getName()).isEqualTo("Tomatensauce");
        });
    }

    @Test
    void domainToEntity_mapsRecipeAndNestedIngredients() {
        ch.tbz.recipe.planner.domain.Ingredient ingredient = new ch.tbz.recipe.planner.domain.Ingredient();
        ingredient.setId(UUID.randomUUID());
        ingredient.setName("Reis");

        Recipe domain = new Recipe();
        domain.setId(UUID.randomUUID());
        domain.setName("Risotto");
        domain.setDescription("Cremiges Risotto");
        domain.setImageUrl("https://example.com/risotto.jpg");
        domain.setIngredients(List.of(ingredient));

        RecipeEntity entity = mapper.domainToEntity(domain);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(entity).isNotNull();
            softly.assertThat(entity.getId()).isEqualTo(domain.getId());
            softly.assertThat(entity.getName()).isEqualTo(domain.getName());
            softly.assertThat(entity.getDescription()).isEqualTo(domain.getDescription());
            softly.assertThat(entity.getImageUrl()).isEqualTo(domain.getImageUrl());
            softly.assertThat(entity.getIngredients()).hasSize(1);
            softly.assertThat(entity.getIngredients().get(0).getName()).isEqualTo("Reis");
        });
    }
}
```

---

### 2.3 Erklärung: Warum SoftAssertions?

**Das Problem bei Standard Assertions:**
Bei Standard-Assertions (`assertEquals(...)` oder `assertThat(...)`) bricht die Testausführung beim **ersten Fehlschlag** sofort mit einer Exception ab. Alle nachfolgenden Überprüfungen werden übersprungen.

**Die Lösung mit `SoftAssertions` (AssertJ):**
1. **Vollständige Fehlerübersicht:** Alle Assertions innerhalb des `assertSoftly(...)`-Blocks werden bis zum Ende ausgeführt. Treten Fehler auf, werden **alle** gesammelten Abweichungen in einer einzigen Fehlermeldung übersichtlich aufgeführt.
2. **Ideal für DTO-/Mapper-Tests:** Objekte mit vielen Attributen können vollständig validiert werden. Man erkennt sofort, ob nur ein einzelnes Feld oder mehrere Felder falsch zugewiesen wurden.
3. **Effizienteres Debugging:** Entwickler sparen Zeit, da Fehler nicht nacheinander in einzelnen Durchläufen entdeckt werden müssen.

---

## 3. Aufgabe 2: Test- & Abdeckungs-Reports

In `pom.xml` (`recipe-planner-fronend-and-backend/recipe-planner-backend/pom.xml`) werden die Reporting-Plugins eingebunden.

### 3.1 JaCoCo Code Coverage konfigurieren

Fügen Sie unter `<plugins>` in der `pom.xml` das `jacoco-maven-plugin` hinzu:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### 3.2 Surefire Test Reports konfigurieren

Fügen Sie das `maven-surefire-report-plugin` zur `pom.xml` hinzu (im `<build><plugins>` Bereich und optional im `<reporting>` Bereich):

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-report-plugin</artifactId>
    <version>3.2.3</version>
    <executions>
        <execution>
            <phase>test</phase>
            <goals>
                <goal>report-only</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### 3.3 Reports lokal generieren und einsehen

Navigieren Sie in das Backend-Verzeichnis und führen Sie die Tests aus:

```bash
cd recipe-planner-fronend-and-backend/recipe-planner-backend
./mvnw clean test
```

Nach erfolgreichem Durchlauf liegen die Reports an folgenden Orten:
- **JaCoCo Coverage:** `recipe-planner-backend/target/site/jacoco/index.html`
- **Surefire Testbericht:** `recipe-planner-backend/target/site/surefire-report.html`

**Reports im Browser öffnen:**
- macOS:
  ```bash
  open target/site/jacoco/index.html
  open target/site/surefire-report.html
  ```
- Windows:
  ```cmd
  start target\site\jacoco\index.html
  start target\site\surefire-report.html
  ```
- Linux:
  ```bash
  xdg-open target/site/jacoco/index.html
  ```

---

## 4. Aufgabe 3: CI/CD Pipeline

### 4.1 GitHub Actions Workflow erstellen

Erstellen Sie die Datei `.github/workflows/ci.yml` im Stammverzeichnis des Repositories:

```yaml
name: CI / CD Pipeline

on:
  push:
    branches: [ "main", "master" ]
  pull_request:
    branches: [ "main", "master" ]

jobs:
  build-and-test:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout Code
        uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven

      - name: Grant execute permission for Maven Wrapper
        run: chmod +x ./recipe-planner-fronend-and-backend/recipe-planner-backend/mvnw

      - name: Run Unit Tests & Generate Reports
        working-directory: recipe-planner-fronend-and-backend/recipe-planner-backend
        run: ./mvnw clean test

      - name: Upload JaCoCo Coverage Report
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: jacoco-coverage-report
          path: recipe-planner-fronend-and-backend/recipe-planner-backend/target/site/jacoco/

      - name: Upload Surefire Test Report
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: surefire-test-report
          path: |
            recipe-planner-fronend-and-backend/recipe-planner-backend/target/site/surefire-report.html
            recipe-planner-fronend-and-backend/recipe-planner-backend/target/surefire-reports/
```

### 4.2 GitLab CI Alternative (optional)

Falls Sie GitLab CI statt GitHub Actions verwenden möchten, erstellen Sie stattdessen eine `.gitlab-ci.yml` im Projekt-Root:

```yaml
image: eclipse-temurin:21-jdk

stages:
  - test

test_backend:
  stage: test
  script:
    - cd recipe-planner-fronend-and-backend/recipe-planner-backend
    - chmod +x ./mvnw
    - ./mvnw clean test
  artifacts:
    when: always
    paths:
      - recipe-planner-fronend-and-backend/recipe-planner-backend/target/site/
      - recipe-planner-fronend-and-backend/recipe-planner-backend/target/surefire-reports/
```

### 4.3 Pipeline testen & Artefakte prüfen

1. Fügen Sie Ihre Änderungen zum Git-Repository hinzu, erstellen Sie einen Commit und pushen Sie auf Ihr Repository:
   ```bash
   git add .
   git commit -m "feat: complete unit tests, reports and ci/cd pipeline"
   git push origin main
   ```
2. Öffnen Sie Ihr GitHub-Repository im Browser und klicken Sie auf den Reiter **Actions**.
3. Klicken Sie auf den aktuellen Pipeline-Lauf.
4. Nach Abschluss finden Sie im Bereich **Artifacts** die herunterladbaren Berichte (`jacoco-coverage-report` und `surefire-test-report`).

---

## 5. Inbetriebnahme & Präsentations-Checkliste

### Backend & Frontend lokal starten

1. **Backend starten:**
   ```bash
   cd recipe-planner-fronend-and-backend/recipe-planner-backend
   ./mvnw spring-boot:run
   ```
   *Läuft unter `http://localhost:8080`*

2. **Frontend starten:**
   ```bash
   cd recipe-planner-fronend-and-backend/recipe-planner-frontend # bzw. recipe-planner-fronend
   npm install
   npm start
   ```
   *Läuft unter `http://localhost:3000`*

### Checkliste für die Lehrperson

- [x] **Aufgabe 1:**
  - MockMvc Tests in `RecipeControllerTest.java` zeigen.
  - Mapper Tests in `IngredientEntityMapperTest.java` & `RecipeEntityMapperTest.java` mit `SoftAssertions` vorführen.
  - Den Vorteil von `SoftAssertions` erklären können.
- [x] **Aufgabe 2:**
  - Lokale Report-Generierung via `./mvnw clean test` vorführen.
  - HTML-Dateien (`jacoco/index.html` und `surefire-report.html`) im Browser zeigen.
- [x] **Aufgabe 3:**
  - GitHub Actions Workflow `.github/workflows/ci.yml` zeigen.
  - Im Browser unter **GitHub Actions** zeigen, dass die Pipeline beim Push grün durchläuft.
  - Heruntergeladene Artefakte (Reports) im GitHub Actions Tab präsentieren.
