# Schnittstellen – Addressbook Backend

Vorgabe: [Unterlagen/schnittstellen/UEBUNGEN.md](https://gitlab.com/ch-tbz-it/Stud/m450/m450/-/blob/main/Unterlagen/schnittstellen/UEBUNGEN.md)
Projekt: [addressbook-backend-v1-1](addressbook-backend-v1-1) (Spring Boot 3.5, Java 21)

Tests ausführen:

```bash
cd addressbook-backend-v1-1
./mvnw test
```

## Aufgabe 1 – Tests für alle Klassen

| Klasse | Test | Was wird geprüft |
|---|---|---|
| `Address` | `repository/AddressTest` | Erstellen von Adressen über beide Konstruktoren, Getter/Setter |
| `AddressService` | `service/AddressServiceTest` | `save`, `getAll` (inkl. Sortierung), `getAddress` gefunden / nicht gefunden |
| `AddressController` | `controller/AddressControllerTest` | HTTP-Status 201 / 200 / 404, Delegation an den Service |
| `AddressComparator` | `util/AddressComparatorTest` | Sortierreihenfolge, `null`-Handling, Sortieren einer Liste |
| `AddressbookApplication` | `AddressbookApplicationTest` | Spring-Kontext startet |

`AddressRepository` ist ein Interface von Spring Data und braucht keinen eigenen Test.

**Mocking der H2-Datenbank:** Im `AddressServiceTest` ersetzt `@Mock AddressRepository` die Datenbank,
`@InjectMocks` gibt den Mock an den Service weiter. Der Service wird damit isoliert getestet. Mit `when(...)`
werden die Rückgabewerte festgelegt, mit `verify(...)` wird geprüft, ob das Repository aufgerufen wurde.
Der `getAll`-Test gibt dem Mock eine **unsortierte** Liste. So wird die Sortierlogik im Service geprüft
und nicht die Datenbank.

**`@BeforeEach`** baut in allen Unit-Tests die Testobjekte vor jedem Test neu auf. Die Tests sind
dadurch unabhängig voneinander.

**Comparator korrekt implementiert:** Die Vorgabe gab immer `-1` zurück. Das verletzt den
`Comparator`-Vertrag (`compare(a, b)` und `compare(b, a)` waren beide negativ). Die erste korrekte
Version vergleicht nach **Nachname**, dann **Vorname**.

## Aufgabe 2 – Comparator erweitert

Bei gleichem Namen vergleicht der Comparator jetzt nach zusätzlichen Attributen:

1. Nachname (ohne Gross-/Kleinschreibung)
2. Vorname
3. **Telefonnummer** *(neu)*
4. **Registrierungsdatum** *(neu)*
5. **ID** *(neu, als letztes Kriterium, damit `0` nur bei wirklich gleichen Adressen zurückkommt)*

Ausserdem werden `null`-Werte (ganze Adresse oder einzelne Felder) immer **ans Ende** sortiert, statt eine
`NullPointerException` auszulösen.

Neue Tests dazu in `AddressComparatorTest`:
`testCompareSameNameDifferentPhonenumber`, `testCompareSameNameAndPhoneDifferentDate`,
`testCompareSameNamePhoneAndDateDifferentId`, `testCompareIdenticalAddresses`,
`testCompareWithNullAddresses`, `testCompareWithNullAttributes`, `testSortingListWithComparator`.
