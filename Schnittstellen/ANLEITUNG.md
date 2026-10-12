# Anleitung Schnittstellen (für mich)

## Tests ausführen

Mit Java 21:

```bash
cd Schnittstellen/addressbook-backend-v1-1
./mvnw test
```

Ohne Java, mit Docker:

```bash
cd Schnittstellen/addressbook-backend-v1-1
docker run --rm -u $(id -u):$(id -g) -e HOME=/tmp -v "$PWD":/app -w /app eclipse-temurin:21-jdk ./mvnw test
```

Erwartet: `Tests run: 20, Failures: 0, Errors: 0`.

## Was am 12.10. geändert wurde

Nur die `pom.xml`: Die zwei Einträge für `mockito-core` (5.19.0) und `junit-jupiter-api` (5.13.4) sind raus.

Der Grund: `spring-boot-starter-test` bringt JUnit und Mockito schon mit, in Versionen, die zueinander passen.
Trägt man eine Bibliothek mit einer eigenen Version ein, hat man z.B. eine neuere JUnit-API, aber eine ältere
JUnit-Engine. Das geht oft gut, kann aber zu Fehlern wie „TestEngine failed to discover tests“ führen.

## Die wichtigsten Punkte zum Erklären

**Mock vs. echte Datenbank** (`AddressServiceTest`):
```java
@Mock AddressRepository repository;     // Fake-Repository, keine H2-Datenbank
@InjectMocks AddressService service;    // Service bekommt den Fake
when(repository.findAll()).thenReturn(unsortierteListe);
verify(repository).findAll();           // wurde das Repository aufgerufen?
```
So teste ich nur die Logik im Service, unabhängig von der Datenbank.

**Comparator-Vertrag:** `compare(a, b)` muss negativ, 0 oder positiv sein, und `compare(b, a)` muss das Gegenteil
liefern. Die Vorgabe hat immer `-1` zurückgegeben, also war `a < b` und gleichzeitig `b < a`. Damit kann
`Collections.sort` nicht richtig sortieren.

**Reihenfolge der Kriterien:** Nachname → Vorname → Telefon → Datum → ID. `null` kommt immer ans Ende.
