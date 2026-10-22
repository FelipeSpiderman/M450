# Health Leveling – Backend

Spring Boot 4.1 mit Java 21 und Maven.

## Starten

```bash
./mvnw spring-boot:run                    # H2 In-Memory (Standard)
```

- Health-Check: http://localhost:8080/actuator/health
- H2-Konsole: http://localhost:8080/h2-console (JDBC-URL `jdbc:h2:mem:healthleveling`, User `sa`)

Mit PostgreSQL:

```bash
cd ..
docker compose up -d
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
```

## Tests und Reports

```bash
./mvnw test        # Unit und Integration Tests (JUnit 5, Mockito, Spring Boot Test)
./mvnw verify      # zusätzlich Reports erzeugen
```

- Coverage (JaCoCo): `target/site/jacoco/index.html`
- Testreport (Surefire): `target/reports/surefire.html`

## Idee und Vorgehen des Backends

### Grundidee

Die Applikation übernimmt die Gesundheitsdaten aus Google Health und erstellt daraus ein Profil, das sich anhand dieser Werte aufleveln lässt. Ziel ist es, möglichst schnell aufzusteigen. Belohnt wird vor allem, wer regelmässig und vollständig Einträge erfasst.

### Ablauf

1. **Daten abrufen:** Zu Beginn werden die vorhandenen Daten aus Google Health abgerufen.
2. **Profil initialisieren:** Aus diesen Daten wird ein Profil mit einem Startlevel berechnet.
3. **Profil speichern:** Das Profil wird in der Datenbank gespeichert, damit es nicht bei jedem Aufruf neu berechnet werden muss.
4. **Einträge erfassen:** Danach erfasst die Benutzerin oder der Benutzer alle manuellen Einträge direkt in dieser Applikation statt in der Google-Health-App. Dazu gehören Aktivitäten, Mahlzeiten, Getränke und das Gewicht.
5. **Leveln:** Jeder Eintrag wird bewertet und gibt XP, mit denen das Profil aufsteigt.

### Leveling-Regeln

- **Grundsatz:** XP werden eher dazugewonnen als abgezogen.
- **Qualität zählt:** Jeder Eintrag gibt XP, die Menge hängt jedoch von der Qualität ab. Beispiel: 15 Minuten Laufen geben bereits gute XP, eine Stunde Laufen gibt noch mehr.
- **Übertreiben wird bestraft:** Wer zum Beispiel deutlich mehr Kalorien zu sich nimmt als empfohlen oder als verbrannt wurden, kann XP verlieren.
- **Tagesrückblick:** Am Ende jedes Tages wird der ganze Tag ausgewertet. Dabei wird geprüft, ob am Morgen, am Mittag und am Abend Einträge erfasst wurden. Wer regelmässig über den Tag verteilt Einträge macht, erhält zusätzliche XP.

### Synchronisation mit Google Health (Idee)

Die Google-Health-App erfasst gewisse Werte automatisch, zum Beispiel Schritte oder Schlaf. Damit die Statistiken in dieser Applikation aktuell bleiben, könnte die API zu festgelegten Zeiten automatisch abgefragt werden.
