# Leitfaden & Erklärung: Modul 450 – Schnittstellen & Test Doubles

Dieses Dokument erklärt alle Konzepte, Aufgaben und Hintergründe des Projekts **Addressbook Backend**, sodass du den Code, die Tests und die theoretischen Grundlagen für das Modul 450 (und eventuelle mündliche/schriftliche Abfragen) vollständig verstehst und erklären kannst.

---

## Inhaltsverzeichnis
1. [Worum geht es in dieser Aufgabe? (Überblick)](#1-worum-geht-es-in-dieser-aufgabe-überblick)
2. [Die Mockito / Java 21 Warnung verstehen (Warum schien es zu "failen"?)](#2-die-mockito--java-21-warnung-verstehen)
3. [Theorie: Test Doubles (Mock, Stub, Spy, Fake, Dummy)](#3-theorie-test-doubles)
4. [Lifecycle von Tests: Mocks vs. Stubs](#4-lifecycle-von-tests-mocks-vs-stubs)
5. [Aufgabe 1: Erklärung & Umsetzung](#5-aufgabe-1-erklärung--umsetzung)
   - [H2-Datenbank wegmocken](#h2-datenbank-wegmocken-im-addressservicetest)
   - [Nutzung von `@BeforeEach`](#nutzung-von-beforeeach)
   - [Korrektur der Comparator-Klasse](#korrektur-der-comparator-klasse)
6. [Aufgabe 2: Erweiterung des Comparators & Tests](#6-aufgabe-2-erweiterung-des-comparators--tests)
7. [Ausführen der Tests](#7-ausführen-der-tests)
8. [Checkliste: Was musst du bei einer Abfrage erklären können?](#8-checkliste-was-musst-du-bei-einer-abfrage-erklären-können)
9. [Abgabe-Guide: Was du im Code konkret herzeigen musst](#9-abgabe-guide-was-du-im-code-konkret-herzeigen-musst)

---

## 1. Worum geht es in dieser Aufgabe? (Überblick)

Das Ziel des Moduls und dieser Übung ist es zu lernen, wie man **Unit-Tests** schreibt, wenn die zu testende Klasse (**SUT = System Under Test**) von anderen Komponenten abhängt (z. B. einer Datenbank oder einem Repository).

Anstatt für jeden Unit-Test eine echte Datenbank (H2) hochzufahren:
- Nutzen wir **Test Doubles (speziell Mocks mit Mockito)**, um das Repository zu simulieren.
- Testen wir den Service isoliert von der Datenbank.
- Implementieren und testen wir eine Sortierlogik (`AddressComparator`) gemäss dem `Comparator`-Interface in Java.

---

## 2. Die Mockito / Java 21 Warnung verstehen

Wenn du die Tests ausführst, siehst du im Terminal folgende Ausgabe:

```text
Mockito is currently self-attaching to enable the inline-mock-maker. This will no longer work in future releases of the JDK.
WARNING: A Java agent has been loaded dynamically (...)
WARNING: If a serviceability tool is in use, please run with -XX:+EnableDynamicAgentLoading to hide this warning
```

### Wichtig: Das ist **kein Test-Fehler / Fail**, sondern eine reine **JVM-Warnung**!
- **Hintergrund:** Ab Java 21 warnt das JDK davor, wenn Java-Bibliotheken (wie ByteBuddy / Mockito) zur Laufzeit dynamisch Java-Agenten laden, um Klassen zu manipulieren (Mocking).
- Am Ende des Testlaufs siehst du trotzdem:
  `Tests run: 20, Failures: 0, Errors: 0, Skipped: 0` -> **BUILD SUCCESS**!
- Alle 20 Tests sind grün und erfolgreich.

---

## 3. Theorie: Test Doubles

Ein **Test Double** ist ein Ersatzobjekt (wie ein Stunt-Double beim Film), das anstelle eines echten Objekts im Test verwendet wird.

Man unterscheidet zwei Hauptkategorien:
1. **Stubs (State Testing):** Fokus liegt auf den gelieferten Daten und dem Zustand.
2. **Mocks (Behavioral Testing):** Fokus liegt auf dem Verhalten und der Interaktion (wurde Methode X aufgerufen?).

### Die 5 Arten von Test Doubles:

| Typ | Was ist es? | Typisches Anwendungsbeispiel |
| :--- | :--- | :--- |
| **Dummy** | Ein Objekt, das nur übergeben wird, um Parameterlisten/Konstruktoren zu füllen. Es wird nie aufgerufen oder verwendet (oft einfach `null` oder ein leeres Objekt). | Ein leerer Logging-Parameter im Konstruktor, der für den eigentlichen Test irrelevant ist. |
| **Stub** | Ein Objekt, das feste, hartcodierte Antworten auf Aufrufe zurückgibt. Es testet nicht das Verhalten, sondern liefert nur die nötigen Daten für den Test. | `when(repo.findAll()).thenReturn(festgelegteListe);` |
| **Spy** | Ein "Spion", der ein echtes Objekt umhüllt und aufzeichnet, welche Methoden mit welchen Parametern aufgerufen wurden. | Echte Methoden ausführen, aber mitzählen, wie oft aufgerufen wurde. |
| **Mock** | Ein künstliches Objekt mit vorprogrammierten Erwartungen. Es überprüft, ob das SUT mit ihm genau so interagiert wie erwartet (Aufrufe, Reihenfolge, Häufigkeit). | `verify(repo, times(1)).save(address);` |
| **Fake** | Eine funktionierende, aber vereinfachte Implementierung des Produktionscodes, die für echte Umgebungen nicht taugt. | Eine `InMemoryAddressRepository`-Klasse mit einer einfachen `ArrayList` statt echter SQL-Datenbank. |

---

## 4. Lifecycle von Tests: Mocks vs. Stubs

### Lifecycle bei Stubs (State Verification):
1. **Setup:** Bereite das SUT und die Stubs mit Testdaten vor.
2. **Execute (Anwenden):** Führe die zu testende Methode auf dem SUT aus.
3. **Verify State:** Prüfe mit `assertEquals(...)` / `assertTrue(...)`, ob das **Ergebnis/der Zustand** korrekt ist.
4. **Teardown:** Ressourcen aufräumen.

### Lifecycle bei Mocks (Behavior Verification):
1. **Setup:** Erstelle das SUT und das Mock-Objekt.
2. **Setup Expectations:** Definiere, was der Mock erwartet bzw. zurückgeben soll (`when(...).thenReturn(...)`).
3. **Execute (Anwenden):** Führe die Methode auf dem SUT aus.
4. **Verify Expectations:** Prüfe mit `verify(...)`, ob das SUT den Mock wie erwartet aufgerufen hat.
5. **Verify State:** Prüfe zusätzlich den Rückgabewert des SUTs.
6. **Teardown:** Ressourcen aufräumen.

---

## 5. Aufgabe 1: Erklärung & Umsetzung

### H2-Datenbank wegmocken im `AddressServiceTest`
Im `AddressServiceTest` wollen wir nur die Geschäftslogik der Klasse `AddressService` testen, **ohne** eine H2-In-Memory-Datenbank zu starten.

```java
@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository; // Mock-Objekt (keine echte DB-Verbindung)

    @InjectMocks
    private AddressService addressService;       // Das SUT erhält das gemockte Repository
```

**Wie wird getestet, dass der Service sortiert?**
Wenn `addressService.getAll()` aufgerufen wird, gibt der Mock absichtlich eine **unsortierte** Liste zurück (`Muster` vor `Adam`):
```java
when(addressRepository.findAll()).thenReturn(List.of(address1, address2));
List<Address> result = addressService.getAll();

// Verifikation: Das Ergebnis muss jetzt sortiert sein (Adam vor Muster)
assertEquals("Adam", result.get(0).getLastname());
assertEquals("Muster", result.get(1).getLastname());
verify(addressRepository, times(1)).findAll();
```
Damit ist bewiesen: **Der Service** sortiert die Daten, nicht die Datenbank!

### Nutzung von `@BeforeEach`
In `setUp()` (annotiert mit `@BeforeEach`) werden vor jedem einzelnen Testfall frische Testobjekte instanziiert. Das garantiert **Test-Isolation** (keine Seiteneffekte zwischen Tests).

### Korrektur der Comparator-Klasse
Die ursprüngliche Vorgabe gab fälschlicherweise immer `-1` zurück (was die mathematische Konsistenz von Comparatoren verletzt: wenn $a < b$, muss $b > a$ gelten).
Korrektur:
1. Nachname vergleichen (`compareToIgnoreCase`).
2. Wenn Nachnamen identisch sind, Vorname vergleichen (`compareToIgnoreCase`).

---

## 6. Aufgabe 2: Erweiterung des Comparators & Tests

In `AddressComparator` wurde die Vergleichslogik kaskadierend erweitert:
1. **Nachname** (`lastname`)
2. **Vorname** (`firstname`)
3. **Telefonnummer** (`phonenumber`)
4. **Registrierungsdatum** (`registrationDate`)
5. **ID** (`id`)

### Null-Safety:
Zusätzlich werden `null`-Werte abgefangen (`nullsLast`), sodass Adressen oder Adress-Felder mit `null` am Ende der sortierten Liste landen, ohne eine `NullPointerException` zu werfen.

### Testfälle in `AddressComparatorTest`:
- Gleicher Nachname, unterschiedlicher Vorname
- Gleicher Vor- und Nachname, unterschiedliche Telefonnummer
- Gleiche Daten, unterschiedliches Registrierungsdatum
- Gleiche Daten, unterschiedliche ID
- Identische Adressen (Rückgabe `0`)
- Umgang mit `null`-Objekten und `null`-Feldern

---

## 7. Ausführen der Tests

Im Terminal:
```bash
cd Schnittstellen/addressbook-backend-v1-1
./mvnw test
```

Ergebnis: **20 Tests bestanden (0 Fehler)**.

---

## 8. Checkliste: Was musst du bei einer Abfrage erklären können?

1. **Was ist ein SUT?**
   - *System Under Test* – die konkrete Klasse/Komponente, die gerade getestet wird (z. B. `AddressService`).
2. **Warum mocken wir das Repository im Unit-Test?**
   - Um den Service unabhängig von der Datenbank schnell und isoliert zu testen. Unit-Tests sollen keine Seiteneffekte durch externe Systeme haben.
3. **Was ist der Unterschied zwischen `@Mock` und `@InjectMocks`?**
   - `@Mock` erstellt eine Attrappe/Ersatzklasse (z. B. für `AddressRepository`).
   - `@InjectMocks` erstellt die echte Instanz der zu testenden Klasse (`AddressService`) und injiziert alle definierten `@Mock`-Objekte hinein.
4. **Was machen `when(...).thenReturn(...)` und `verify(...)`?**
   - `when(...).thenReturn(...)`: Stubbing – legt fest, was der Mock bei einem Methodenaufruf antworten soll.
   - `verify(...)`: Behavior-Verifikation – überprüft, ob eine Methode auf dem Mock aufgerufen wurde (und wie oft).
5. **Wie funktioniert der `AddressComparator`?**
   - Er vergleicht schrittweise die Attribute (Nachname $\rightarrow$ Vorname $\rightarrow$ Telefon $\rightarrow$ Datum $\rightarrow$ ID) und schiebt `null`-Werte ans Ende.

---

## 9. Abgabe-Guide: Was du im Code konkret herzeigen musst

Hier ist die genaue Schritt-für-Schritt-Anleitung, was du bei der Abgabe oder Präsentation im Code öffnen, zeigen und sagen solltest, um alle geforderten Punkte lückenlos nachzuweisen:

---

### Schritt 1: Tests für alle Klassen vorweisen (Aufgabe 1)
Öffne im Projektbaum den Ordner `src/test/java/ch/tbz/m450/`:
Zeige die 5 Testklassen:
1. `AddressTest.java`: Testet die Datenklasse / Entity (Konstruktoren, Getter & Setter).
2. `AddressServiceTest.java`: Testet die Geschäftslogik mit Mocks.
3. `AddressControllerTest.java`: Testet die REST-Endpunkte (`GET`, `POST`, Status-Codes 200, 201, 404).
4. `AddressComparatorTest.java`: Testet alle Sortier- und Vergleichskriterien.
5. `AddressbookApplicationTest.java`: Testet das Bootstrapping der Spring-Boot-Anwendung (`contextLoads`).

> **Wichtig zu erwähnen:** Das `AddressRepository` ist ein reines Spring Data JPA Interface (ohne eigene Methoden-Implementierung) und wird daher in Unit-Tests gemockt, statt dafür separate Tests zu schreiben.

---

### Schritt 2: `@BeforeEach` Annotation vorzeigen (Aufgabe 1)
Öffne `AddressServiceTest.java` (Zeilen 32–37) oder `AddressComparatorTest.java` (Zeilen 18–22):
```java
@BeforeEach
void setUp() {
    now = new Date();
    address1 = new Address(1, "Max", "Muster", "0791234567", now);
    address2 = new Address(2, "Anna", "Adam", "0789876543", now);
}
```
**Was du dazu erklärst:**
- *„Mit `@BeforeEach` stellen wir sicher, dass vor jedem einzelnen Testfall frische, saubere Testdaten erzeugt werden. Dadurch beeinflussen sich die Tests nicht gegenseitig (Test-Isolation).“*

---

### Schritt 3: Adressen erstellen und testen (Aufgabe 1)
Öffne `AddressTest.java`:
- Zeige `testAddressConstructorAndGetters()`: Testet das Erstellen mit Parametern.
- Zeige `testAddressSetters()`: Testet das Erstellen mit dem Standard-Konstruktor und das Setzen der Werte über Setter.

---

### Schritt 4: H2-Datenbank wegmocken & Service testen (Aufgabe 1)
Öffne `AddressServiceTest.java`:
1. **Mock-Setup (Klassenanfang):**
   - Zeige `@ExtendWith(MockitoExtension.class)`.
   - Zeige `@Mock private AddressRepository addressRepository;` $\rightarrow$ Das ist der Ersatz für die H2-Datenbank.
   - Zeige `@InjectMocks private AddressService addressService;` $\rightarrow$ Mockito setzt den Mock automatisch in den Service ein.
2. **Methode `testGetAllAddressesSorted()` vorzeigen:**
   ```java
   when(addressRepository.findAll()).thenReturn(List.of(address1, address2));
   List<Address> result = addressService.getAll();
   assertEquals("Adam", result.get(0).getLastname());
   verify(addressRepository, times(1)).findAll();
   ```
   **Was du dazu erklärst:**
   - *„Hier mocken wir die DB weg: `when(findAll())` gibt absichtlich eine unsortierte Liste zurück (`Muster` vor `Adam`). Im Assert prüfen wir, dass `getAll()` die Liste sortiert (`Adam` vor `Muster`). Mit `verify()` stellen wir sicher, dass das Repository genau 1x aufgerufen wurde.“*
3. **Weitere Service-Methoden zeigen:**
   - `testSaveAddress()`: Speichern mit Mock-Rückgabe und `verify`.
   - `testGetAddressById_Found()` / `testGetAddressById_NotFound()`: Umgang mit `Optional.of` und `Optional.empty()`.

---

### Schritt 5: Comparator-Klasse korrigieren & erweitern (Aufgabe 1 & Aufgabe 2)
Öffne `AddressComparator.java` (in `src/main/java/ch/tbz/m450/util/`):
1. **Korrektur erklären:**
   - *„In der Vorlage gab die Methode immer `-1` zurück. Das war mathematisch inkonsistent. Wir haben das behoben, sodass Vor- und Nachname mit `compareToIgnoreCase` verglichen werden.“*
2. **Erweiterung um zusätzliche Attribute (Aufgabe 2):**
   - Zeige die Kaskade im Code:
     1. `lastname` (Nachname)
     2. `firstname` (Vorname)
     3. `phonenumber` (Telefon)
     4. `registrationDate` (Datum)
     5. `id` (Eindeutige ID)
3. **Null-Safety herzeigen:**
   - Zeige die Behandlung von `null`-Objekten (`a1 == null`, `a2 == null`) und die Hilfsmethode `compareNullable()` für Textfelder sowie Datumsfelder.

Öffne `AddressComparatorTest.java`:
- Zeige die 9 Testmethoden, die jeden einzelnen Fall isoliert beweisen (z. B. gleicher Nachname aber anderer Vorname, gleiche Namen aber andere Telefonnummer, `null`-Werte, `list.sort()`).

---

### Schritt 6: Live-Testdurchlauf im Terminal vorführen
Führe im Terminal im Verzeichnis `addressbook-backend-v1-1` aus:
```bash
./mvnw test
```
Zeige das Ergebnis:
- **`Tests run: 20, Failures: 0, Errors: 0, Skipped: 0`**
- **`BUILD SUCCESS`**
- (Falls die Java 21 ByteBuddy/Mockito Warnung erscheint, erkläre kurz: *„Das ist eine normale JDK 21 Sicherheitswarnung über dynamische Java-Agents, kein Testfehler – alle 20 Tests sind grün.“*)
