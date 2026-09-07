# JUnit 5 – Zusammenfassung der gängigsten Features

JUnit 5 ist das aktuellste Framework für Unit-Tests in Java. Es baut auf Java 8+ auf und bringt viele bequeme Features mit. In diesem Dokument sind die wichtigsten davon kurz zusammengefasst – jeweils mit einem kleinen Beispiel aus unserer Calculator- und Bank-Aufgabe.

---

## 1. Import und Testannotation `@Test`

Jede Testmethode wird mit `@Test` markiert. Nur so weiss JUnit, dass eine Methode ein Test ist.

```java
import org.junit.jupiter.api.Test;

class CalculatorTest {
    @Test
    void testAddition() {
        // Test-Code
    }
}
```

**Anwendungsfall:** Jede Methode, die eine einzelne Eigenschaft absichert, bekommt ihr eigenes `@Test`.

---

## 2. Lifecycle-Annotationen: `@BeforeEach`, `@AfterEach`, `@BeforeAll`, `@AfterAll`

Diese Annotationen definieren Code, der vor/nach jedem Test (bzw. vor/nach allen Tests) ausgeführt wird. Damit vermeiden wir Wiederholungen.

```java
import org.junit.jupiter.api.BeforeEach;

class CalculatorTest {
    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();  // frischer Rechner vor jedem Test
    }
}
```

**Anwendungsfall:** Gemeinsame Vorbereitung (z. B. ein neues Konto oder ein neues Bank-Objekt anlegen), damit jeder Test von einem sauberen Zustand ausgeht.

---

## 3. Assertions – die Behauptungen

Assertions überprüfen, ob das tatsächliche Resultat der Erwartung entspricht. `assertEquals` ist die bekannteste davon.

```java
import static org.junit.jupiter.api.Assertions.*;

assertEquals(5.0, calculator.add(2.0, 3.0), 0.0001);
assertTrue(konto.withdraw(13576, 5000));
assertFalse(konto.withdraw(13576, -5000));
assertThrows(ArithmeticException.class, () -> calculator.divide(5.0, 0.0));
```

| Assertion | Bedeutung |
|-----------|-----------|
| `assertEquals(expected, actual)` | Werte sind gleich (bei `double` mit Toleranz) |
| `assertTrue` / `assertFalse` | Bedingung ist wahr / falsch |
| `assertNull` / `assertNotNull` | Objekt ist null / nicht null |
| `assertThrows` | Es wird eine bestimmte Exception erwartet |

**Anwendungsfall:** Nach einer Einzahlung prüfen, dass der Kontostand stimmt, oder prüfen, dass eine ungültige Eingabe abgelehnt wird.

---

## 4. `@DisplayName` – sprechende Namen

Damit erhalten Tests ausgeschriebene, verständliche Namen statt Methoden-Namen.

```java
@Test
@DisplayName("Division durch null wirft Exception")
void testDivideDurchNull() { ... }
```

**Anwendungsfall:** Im Test-Report erkennt man sofort, was ein Test aussagen soll – auch für Nicht-Programmierer.

---

## 5. Parameterisierte Tests mit `@ParameterizedTest` und `@CsvSource`

Ein Test wird automatisch mit mehreren Werte-Paaren ausgeführt. Das spart viel Code, wenn man ein und dieselbe Regel mit vielen Ein-/Ausgabewerten prüfen will.

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@ParameterizedTest
@CsvSource({
    "1, 1, 2",
    "2, 3, 5",
    "-3, 3, 0"
})
void testAddParameterized(double a, double b, double expected) {
    assertEquals(expected, calculator.add(a, b), 0.0001);
}
```

**Anwendungsfall:** Alle Grenzwerte einer Rabatt- oder Berechnungsregel in einem einzigen Test abdecken.

---

## 6. Testklassen: Mindest-Sichtbarkeit

In JUnit 5 dürfen Testklassen und Testmethoden auch `package-private` sein (ohne `public`). Das ist kürzer als in JUnit 4.

```java
class CalculatorTest {   // kein public nötig
    @Test
    void testAdd() { ... }
}
```

**Anwendungsfall:** Die Calculator-Testklasse nutzt dies – die Methoden sind bewusst ohne `public` geschrieben.

---

## 7. Tests ausführen

- **In der IDE:** Rechtsklick auf die Testklasse → Run. Im Bank-Projekt laufen alle Tests beim Maven-Befehl mit.
- **Mit Maven:**
  ```
  ./mvnw test
  ```
  Maven führt alle Tests im Ordner `src/test/java` aus und generiert im Unterordner `target/surefire-reports` einen Report.

**Anwendungsfall:** Auf der Kommandozeile ausführen, damit die Tests auch automatisiert (z. B. in einer CI/CD-Pipeline) laufen.

---

## 8. Code Coverage mit JaCoCo

JaCoCo misst, wie viel vom Produktivcode von den Tests abgedeckt wird. Das zeigt auf, welche Stellen noch ungetestet sind.

```
./mvnw test
```

Danach liegt der Report unter `target/site/jacoco/index.html`.

**Anwendungsfall:** Nach dem Schreiben der Bank-Tests haben wir eine Zeilen-Coverage von rund 90% erreicht – nur die reine `Main`-Starterklasse ist ungetestet.

---

## Referenz

Eine ausführliche und gut verständliche JUnit-Anleitung findest du hier:

- **Vogella – JUnit 5 Tutorial:** https://www.vogella.com/tutorials/JUnit/article.html
- **Offizielle JUnit 5 Doku:** https://junit.org/junit5/docs/current/user-guide/