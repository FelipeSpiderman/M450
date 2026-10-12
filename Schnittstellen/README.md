# Schnittstellen – Addressbook Backend

Aufgabe: [Unterlagen/schnittstellen/UEBUNGEN.md](https://gitlab.com/ch-tbz-it/Stud/m450/m450/-/blob/main/Unterlagen/schnittstellen/UEBUNGEN.md)
Code: [addressbook-backend-v1-1](addressbook-backend-v1-1) (Spring Boot 3.5, Java 21)

```bash
cd addressbook-backend-v1-1
./mvnw test
```

Ergebnis: 20 Tests, alle grün.

## Aufgabe 1

Für jede Klasse gibt es eine Testklasse:

- `AddressTest`: Adressen über beide Konstruktoren erstellen, Getter und Setter
- `AddressServiceTest`: speichern, alle holen (inkl. Sortierung), eine holen (gefunden / nicht gefunden)
- `AddressControllerTest`: richtige HTTP-Statuscodes (201, 200, 404)
- `AddressComparatorTest`: Sortierreihenfolge und `null`-Werte
- `AddressbookApplicationTest`: die Applikation startet

Das `AddressRepository` ist nur ein Spring-Data-Interface, darum hat es keinen eigenen Test.

Im `AddressServiceTest` ist die H2-Datenbank weggemockt. Mit `@Mock` wird das Repository ein Mock, `@InjectMocks`
gibt ihn dem Service mit. Bei `getAll` gibt der Mock die Adressen absichtlich unsortiert zurück. So sieht man, dass
wirklich der Service sortiert und nicht die Datenbank.

`@BeforeEach` erstellt vor jedem Test die Testdaten neu, damit sich die Tests nicht gegenseitig beeinflussen.

Die vorgegebene Comparator-Klasse hat immer `-1` zurückgegeben, egal was man verglichen hat. Richtig ist: zuerst
nach Nachname vergleichen, bei Gleichstand nach Vorname (Gross-/Kleinschreibung egal).

## Aufgabe 2

Den Comparator habe ich erweitert. Bei gleichem Namen wird jetzt weiter verglichen nach:

1. Telefonnummer
2. Registrierungsdatum
3. ID (damit nur bei wirklich gleichen Adressen `0` herauskommt)

Ausserdem kommen `null`-Werte immer ans Ende der Liste. Vorher gab es dort eine `NullPointerException`.

Die neuen Fälle sind in `AddressComparatorTest` getestet: unterschiedliche Telefonnummer, unterschiedliches Datum,
unterschiedliche ID, identische Adressen, `null`-Adressen, `null`-Felder und das Sortieren einer ganzen Liste.

## Nachtrag 12.10.2026

In der `pom.xml` waren `mockito-core` und `junit-jupiter-api` mit eigenen Versionen eingetragen. Spring Boot bringt
über `spring-boot-starter-test` aber schon passende Versionen mit. Gemischte Versionen können dazu führen, dass
JUnit die Tests nicht findet. Darum habe ich die zwei Einträge entfernt.

**KI-Nutzung:** Diese README und die Korrektur an der `pom.xml` sind mit Claude Code (KI-Agent) entstanden.
