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

## Umsetzung

### Endpunkte

| Methode | Pfad | Zweck |
|---|---|---|
| GET | `/api/profile` | Level, XP und Fortschritt in Prozent |
| GET | `/api/days` | Alle Tage mit Werten, Einträgen und XP, neuester zuerst |
| GET | `/api/days/{datum}` | Ein Tag mit Werten, Einträgen und XP-Aufschlüsselung |
| POST | `/api/days` | Tageswerte manuell erfassen (`date`, `steps`, `sleepScore`, `activityCount`, `activityMinutes`) |
| GET | `/api/entries?date=` | Einträge eines Tages |
| POST | `/api/entries` | Eintrag erfassen (`ACTIVITY`, `MEAL`, `DRINK`, `WEIGHT`) |
| DELETE | `/api/entries/{id}` | Eintrag löschen |
| POST | `/api/import` | Datei (`file`) als `.csv` oder `.json` importieren |
| POST | `/api/sync?from=&to=` | Daten von Google Health holen (ohne Parameter: letzte 7 Tage) |

Fehler werden als JSON mit `status` und `detail` zurückgegeben: 400 bei ungültigen Eingaben, 404 wenn etwas nicht existiert, 503 wenn Google Health nicht konfiguriert oder nicht erreichbar ist.

### XP-Regeln

| Quelle | XP |
|---|---|
| Schritte | 5 XP pro 1000 Schritte (max. 20'000 gezählt), +20 ab 10'000 |
| Schlaf-Score | Score / 2 (also 0–50) |
| Importierte Aktivitäten | 10 pro Aktivität + 1 pro Minute (max. 120 Minuten) |
| Aktivität (Eintrag) | 10 + 1 pro Minute (max. 120 Minuten) |
| Mahlzeit | 10 |
| Getränk | 5 pro 250 ml (max. 20) |
| Gewicht | 5 |
| Tagesrückblick | Einträge in 2 Tageszeiten +10, in allen 3 +30 (Morgen 05–11, Mittag 11–17, Abend 17–23 Uhr) |
| Zu viel gegessen | −20, wenn die Kalorien der Mahlzeiten > 2500 + verbrannte Kalorien der Aktivitäten |

**Level:** Von Level n auf n+1 braucht es `100 × n` XP (Level 2 ab 100 XP, Level 3 ab 300, Level 4 ab 600 …). Die Gesamt-XP fallen nie unter 0.

Das Profil wird nicht gespeichert: XP und Level werden bei jeder Abfrage aus den gespeicherten Tagen und Einträgen berechnet. Gespeichert werden nur die Rohdaten, dadurch können die XP nie falsch «hängen bleiben», wenn ein Eintrag gelöscht oder ein Import wiederholt wird.

### Importformat

CSV mit der Kopfzeile `date,steps,sleep_score,activity_count,activity_minutes` (siehe `Projekt/IMPORT_FORMAT.md`, Abschnitt 6) oder JSON:

```json
[ { "date": "2026-10-06", "steps": 8412, "sleepScore": 72, "activityCount": 1, "activityMinutes": 69 } ]
```

Leere Werte bleiben leer (0 Schritte ≠ keine Daten). Ein bereits vorhandener Tag wird aktualisiert, nicht doppelt angelegt.

### Google Health

Die Zugangsdaten werden als Umgebungsvariablen gesetzt: `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`, `GOOGLE_REFRESH_TOKEN` (Refresh Token z. B. mit `Projekt/api-explorer/get-token.mjs` holen). Abgerufen werden Schritte und Schlaf. Da die API keinen Schlaf-Score liefert, wird er aus der Schlafdauer geschätzt: Minuten geschlafen / 480 × 100, max. 100.
