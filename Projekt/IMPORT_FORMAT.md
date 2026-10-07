# Google Health Export – Importformat

Dokumentation des Google-Takeout-Exports («Google Health»), Stand Oktober 2026.
Die Beispielzeilen sind anonymisiert (IDs und Werte verändert), Spaltennamen und Formate sind original.

Der Export enthält rund 600 Dateien in 29 Ordnern. Für das Level-System sind nur **drei Werte pro Tag** relevant:
Schritte, Schlaf-Score und Aktivitäten. Alles andere wird ignoriert.

---

## 1. Relevante Dateien (Übersicht)

| Wert | Empfohlene Quelle | Alternative |
|---|---|---|
| Schritte | `Global Export Data/steps-YYYY-MM-DD.json` | `Physical Activity_GoogleData/Steps/steps_YYYY-MM-01.csv` |
| Schlaf-Score | `Sleep Score/sleep_score.csv` | `Health Fitness Data_GoogleData/UserSleepScores_YYYY-MM-DD.csv` |
| Schlaf-Details | `Global Export Data/sleep-YYYY-MM-DD.json` | `Health Fitness Data_GoogleData/UserSleeps_YYYY-MM-DD.csv` |
| Aktivitäten | `Global Export Data/exercise-N.json` | `Health Fitness Data_GoogleData/UserExercises_YYYY-MM-DD.csv` |
| Active Zone Minutes (optional) | `Physical Activity_GoogleData/Active Zone Minutes/active_zone_minutes_YYYY-MM-01.csv` | – |

**Datum im Dateinamen** = Startdatum des Exports bzw. des Monats, *nicht* der Tag der Daten.
Eine Datei kann also mehrere Tage oder einen ganzen Monat enthalten. Ein Import muss deshalb immer alle Dateien eines Musters lesen (`steps-*.json`, nicht eine bestimmte Datei).

### ⚠️ Zeitzonen – wichtigster Stolperstein

| Quelle | Zeitformat | Zeitzone |
|---|---|---|
| `Global Export Data/*.json` | `MM/dd/yy HH:mm:ss` oder `yyyy-MM-ddTHH:mm:ss.SSS` | **lokal** (kein `Z`) |
| alle `*.csv` | `yyyy-MM-ddTHH:mm:ssZ` | **UTC** (`Z`), teilweise mit Spalte `utc_offset` (`+02:00`) |

Für «Schritte pro Tag» muss das Datum in **lokaler Zeit** bestimmt werden, sonst landen Schritte zwischen 00:00 und 02:00 (Sommerzeit) im falschen Tag.

---

## 2. Schritte

### `Global Export Data/steps-YYYY-MM-DD.json` (empfohlen)

Ein Array mit einem Eintrag **pro Minute**, nur Daten vom Tracker (Fitbit). `value` ist ein **String**.

```json
[
  { "dateTime": "09/26/26 20:16:00", "value": "0" },
  { "dateTime": "09/26/26 20:18:00", "value": "13" }
]
```

- `dateTime`: lokale Zeit, Format `MM/dd/yy HH:mm:ss` (amerikanisch, zweistelliges Jahr)
- Tagessumme = Summe aller `value` mit gleichem Datum

### `Physical Activity_GoogleData/Steps/steps_YYYY-MM-01.csv` (Alternative)

Eine Datei pro Monat, ältere Daten (ab Juni 2026) sind nur hier vorhanden.

```csv
timestamp,steps,data source
2026-10-01T06:14:03.552356Z,51,Phone Health Kit
2026-10-01T06:15:00Z,84,Google Fitbit Air
```

> ⚠️ Enthält **zwei Quellen** (`Google Fitbit Air` und `Phone Health Kit`). Werden beide summiert, werden Schritte doppelt gezählt.
> Beim Import nach `data source` filtern (nur Tracker) oder pro Tag die grössere Summe nehmen.

---

## 3. Schlaf

### `Sleep Score/sleep_score.csv` (empfohlen für den Score)

Eine Zeile pro Nacht, **direkt der Schlaf-Score 0–100**, wie in der App angezeigt.

```csv
sleep_log_entry_id,timestamp,overall_score,composition_score,revitalization_score,duration_score,deep_sleep_in_minutes,resting_heart_rate,restlessness
50000000001,2026-10-07T04:58:30Z,74,,74,,47,53,0.0873
50000000002,2026-10-06T04:57:30Z,72,,72,,71,53,0.1230
```

- `timestamp` = Aufwachzeit in UTC → lokales Datum davon = «Schlaf der letzten Nacht» für diesen Tag
- `overall_score` = Ganzzahl → **XP-Wert Schlaf**
- leere Felder (`composition_score`, `duration_score`) kommen vor → beim Parsen erlauben

### `Health Fitness Data_GoogleData/UserSleepScores_*.csv` (Alternative)

Gleicher Score, aber als Dezimalzahl (`85.1797…`) und mit `-1` für fehlende Teil-Scores. Verknüpft über `sleep_id` mit `UserSleeps_*.csv`.

```csv
sleep_id,sleep_score_id,data_source,score_utc_offset,score_time,overall_score,duration_score,composition_score,revitalization_score,sleep_time_minutes,deep_sleep_minutes,rem_sleep_percent,resting_heart_rate,sleep_goal_minutes,...
1000000000000000001,2000000000000000001,DERIVED,+02:00,2026-10-01T02:59:00Z,85.18,-1,-1,-1,413,64,25.43,53,420,...
```

### `Global Export Data/sleep-YYYY-MM-DD.json` (Details)

Pro Nacht ein Objekt mit Dauer, Effizienz und Schlafphasen. **Enthält keinen Score.**

```json
{
  "logId": 50000000001,
  "dateOfSleep": "2026-10-07",
  "startTime": "2026-10-06T21:32:30.000",
  "endTime": "2026-10-07T04:58:30.000",
  "duration": 26760000,
  "minutesAsleep": 389,
  "minutesAwake": 57,
  "timeInBed": 446,
  "efficiency": 93,
  "type": "stages",
  "logType": "auto_detected",
  "levels": {
    "summary": { "deep": { "minutes": 47 }, "light": { "minutes": 318 }, "rem": { "minutes": 24 }, "wake": { "minutes": 57 } },
    "data": [ { "dateTime": "2026-10-06T21:32:30.000", "level": "light", "seconds": 1560 } ]
  }
}
```

- `dateOfSleep` ist bereits das lokale Datum, praktisch zum Verknüpfen
- `duration` in **Millisekunden**
- Nickerchen erscheinen als eigene Einträge (`type: "classic"`, kurz) → für den Tageswert ignorieren oder nur den längsten Schlaf nehmen

---

## 4. Aktivitäten

### `Global Export Data/exercise-N.json` (empfohlen)

Ein Array mit allen Aktivitäten (N = 0, 1, … bei vielen Einträgen).

```json
{
  "logId": 70000000001,
  "activityName": "Outdoor Bike",
  "activityTypeId": 1071,
  "startTime": "09/27/26 05:22:23",
  "duration": 2560000,
  "activeDuration": 2560000,
  "calories": 368,
  "averageHeartRate": 118,
  "logType": "auto_detected",
  "activeZoneMinutes": { "totalMinutes": 31, "minutesInHeartRateZones": [ ... ] },
  "heartRateZones": [ ... ],
  "activityLevel": [ ... ]
}
```

- `startTime`: lokale Zeit, Format `MM/dd/yy HH:mm:ss`
- `duration` / `activeDuration` in **Millisekunden** → `/ 60000` = Minuten
- `logType`: `auto_detected` oder `manual`

### `Health Fitness Data_GoogleData/UserExercises_*.csv` (Alternative)

46 Spalten, die wichtigen:

```csv
exercise_id,exercise_start,exercise_end,utc_offset,...,activity_name,log_type,...
1000000000000000001,2026-10-04T12:54:29Z,2026-10-04T14:03:06Z,+02:00,...,Bike,AUTO_DETECTED,...
```

- Dauer = `exercise_end - exercise_start`
- Vorkommende `activity_name`: `Bike`, `Outdoor Run`, `Outdoor Walk`, `Weight Machines`, `Sport`
- Die Spalte `activity_type_probabilities` enthält Kommas innerhalb von Anführungszeichen → **echten CSV-Parser** verwenden, nicht `split(",")`

---

## 5. Ignorierte Ordner

| Ordner | Grund |
|---|---|
| `Account Changes`, `User Security Data`, `Your Profile`, `User Profile_GoogleData` | Kontodaten, keine Gesundheitswerte |
| `Commerce_GoogleData`, `Fitbit Premium`, `Discover`, `Guided Programs`, `Social`, `Social_GoogleData`, `InAppNotifications_GoogleData`, `Email Notifications Settings_GoogleData` | App-/Kontoeinstellungen, nur READMEs oder Einstellungen |
| `Paired Devices` | Geräteinfos (gibt es auch über die API: `/users/me/pairedDevices`) |
| `Heart Rate`, `Heart Rate Variability`, `Oxygen Saturation (SpO2)`, `Temperature`, `Biometrics`, `Atrial Fibrillation PPG` | Vitalwerte, nicht Teil der XP-Regeln |
| `Daily Readiness`, `Stress Score`, `Stress Journal`, `Snore and Noise Detect`, `Menstrual Health` | nicht Teil der XP-Regeln |
| `Health Fitness Data_GoogleData` (ausser Sleep/Exercise) | interne Daten (Sensor-Tokens, Aktivitätswahrscheinlichkeiten, Einstellungen) |
| `Physical Activity_GoogleData` (ausser Steps/AZM) | Minutenwerte für Kalorien, Distanz, Puls usw. |

---

## 6. Zusammenführen zu einer Datei (für ein späteres Skript)

Damit die Hauptapplikation nicht jede Export-Datei einzeln verarbeiten muss, kann ein Skript den Export in **eine** CSV-Datei zusammenfassen, die dann über den normalen CSV-Import eingelesen wird:

```csv
date,steps,sleep_score,activity_count,activity_minutes
2026-10-06,8412,72,1,69
2026-10-07,2103,74,0,0
```

| Spalte | Quelle | Berechnung |
|---|---|---|
| `date` | – | lokales Datum `yyyy-MM-dd` |
| `steps` | `Global Export Data/steps-*.json` | Summe aller `value` (als Zahl) mit gleichem lokalen Datum aus `dateTime` |
| `sleep_score` | `Sleep Score/sleep_score.csv` | `overall_score`; Datum = lokales Datum von `timestamp` (UTC + Offset). Leer, wenn keine Messung |
| `activity_count` | `Global Export Data/exercise-*.json` | Anzahl Einträge mit gleichem Datum aus `startTime` |
| `activity_minutes` | `Global Export Data/exercise-*.json` | Summe `activeDuration / 60000`, gerundet |

Hinweise für das Skript:
- Alle Dateien eines Musters lesen (Globs), Tage über mehrere Dateien zusammenführen
- Fehlende Werte leer lassen, nicht `0` setzen (0 Schritte ≠ keine Daten). Das ist auch ein guter Grenzfall für die Tests.
- Das Datumsformat `MM/dd/yy` ausdrücklich parsen (nicht dem Standard-Parser überlassen)
- Werkzeug: Node (`fs`, `JSON.parse`) oder Python (`csv`, `json`) eignen sich besser als reines Bash, weil JSON und CSV mit Anführungszeichen geparst werden müssen
