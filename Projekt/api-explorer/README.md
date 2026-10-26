# API Explorer – Google Health API ausprobieren

Kleines Werkzeug, um die Google Health API mit den eigenen Daten zu testen und die **echten Antworten (Payloads)** anzuschauen, bevor die Anbindung in Spring Boot gebaut wird.
Es braucht nur Node.js (≥ 18), keine npm-Pakete.

| Datei | Zweck |
|---|---|
| `get-token.mjs` | Einmalige Anmeldung bei Google, speichert den Refresh Token in `.env` |
| `explore.mjs` | Ruft die relevanten Endpunkte auf und speichert jede Antwort in `samples/<name>.json` |
| `requests.http` | Dieselben Requests zum Ausprobieren von Hand (VS Code «REST Client» / IntelliJ) |
| `.env.example` | Vorlage für die Zugangsdaten |
| `samples/` | Ausgabe, **nicht im Git** (enthält persönliche Gesundheitsdaten) |

Die Endpunkte selbst sind in [`../ENDPOINTS.md`](../ENDPOINTS.md) beschrieben.

**Kurzfassung – was muss ich tun?**

1. Einmalig: Google Cloud Console einrichten (Abschnitt 1) und `.env` befüllen (Abschnitt 2)
2. Einmalig bzw. alle 7 Tage: `node get-token.mjs`
3. So oft man will: `node explore.mjs` → Antworten in `samples/` anschauen

---

## So funktionieren die Skripte

Die Google Health API verlangt **OAuth**: Man muss als Person zustimmen, dass die App die eigenen Daten lesen darf. Ein API-Key reicht dafür nicht.

| Begriff | Bedeutung | Gültig |
|---|---|---|
| Client ID + Client Secret | «Ausweis» der App, aus der Cloud Console | unbegrenzt |
| Refresh Token | Erlaubnis, ohne neue Anmeldung Access Tokens zu holen | 7 Tage (Testing-Modus) |
| Access Token | Wird bei jedem Request als `Authorization: Bearer …` mitgeschickt | ca. 1 Stunde |

**`get-token.mjs`** (Anmeldung)
1. Liest Client ID und Secret aus `.env`.
2. Baut die Google-Anmelde-URL mit allen Scopes und gibt sie im Terminal aus.
3. Startet einen kleinen Webserver auf `http://localhost:8765/callback` und wartet.
4. Nach dem Klick auf «Zulassen» leitet Google den Browser dorthin weiter, mit einem einmaligen `code` in der URL.
5. Das Skript tauscht diesen Code bei Google gegen einen **Refresh Token** und schreibt ihn als `GOOGLE_REFRESH_TOKEN` in `.env`.

**`explore.mjs`** (Requests)
1. Holt mit dem Refresh Token einen frischen **Access Token**.
2. Ruft nacheinander die Endpunkte aus der Liste `ENDPOINTS` im Skript auf. Zeitraum: `FROM_DATE`/`TO_DATE` aus `.env`, sonst die letzten 7 Tage.
3. Liest bei Bedarf mehrere Seiten (`nextPageToken`, max. 3 Seiten).
4. Wiederholt den Request bei `429`/`504` mit Wartezeit, und bei einem abgelehnten Filter (`400`) ohne Filter.
5. Speichert jede Antwort in `samples/` und gibt pro Endpunkt eine Zeile mit ✓/✗ aus.

**`env.mjs`** ist nur ein Hilfsmodul, das `.env` liest und schreibt. Dadurch braucht es keine npm-Pakete.

---

## 1. Google Cloud Console vorbereiten (einmalig)

In <https://console.cloud.google.com> das bestehende Projekt auswählen:

1. **API aktivieren:** *APIs & Services → Library* → «Google Health API» suchen → **Enable**.
2. **OAuth-Zustimmungsbildschirm:** *APIs & Services → OAuth consent screen* (bzw. *Google Auth Platform*)
   - User Type: **External**
   - Publishing status: **Testing** lassen
   - Unter **Test users** die eigene Google-Adresse eintragen (die mit dem Fitbit verbunden ist). Ohne diesen Eintrag lehnt Google die Anmeldung ab (`access_denied`).
   - Unter **Data access / Scopes** die 6 `googlehealth.*.readonly`-Scopes aus `ENDPOINTS.md` hinzufügen.
3. **OAuth-Client erstellen:** *APIs & Services → Credentials → Create credentials → OAuth client ID*
   - Application type: **Webanwendung** (Web application)
   - Unter **Autorisierte Weiterleitungs-URIs** eintragen:
     - `http://localhost:8765/callback` (für `get-token.mjs`, genau so: `http`, kein `/` am Schluss)
     - optional `https://developers.google.com/oauthplayground` (für den OAuth Playground)
   - Client ID und Client Secret kopieren. Wird das Secret nicht mehr angezeigt, im Client ein neues Secret hinzufügen.
   - Alternativ geht auch der Typ **Desktop app**, dort ist `localhost` automatisch erlaubt.
   - Die heruntergeladene Credentials-JSON wird nicht gebraucht (sie enthält dieselben Werte) und darf **nicht** ins Git.

## 2. Zugangsdaten eintragen

```bash
cd Projekt/api-explorer
cp .env.example .env
# .env öffnen und GOOGLE_CLIENT_ID und GOOGLE_CLIENT_SECRET eintragen
```

`.env` ist in `.gitignore` eingetragen und darf **nie** committet werden.

## 3. Anmelden und Refresh Token holen

```bash
node get-token.mjs
```

Das Skript zeigt eine URL an. Diese im Browser öffnen, mit dem Fitbit-Google-Konto anmelden und alle Berechtigungen erlauben.
Bei der Warnung «Google hasn't verified this app» → *Continue* (das ist normal im Testing-Modus).
Danach leitet Google auf `http://localhost:8765/callback` weiter, und das Skript schreibt `GOOGLE_REFRESH_TOKEN` in die `.env`.

> Unter WSL öffnet der Windows-Browser `localhost:8765` normalerweise direkt im WSL. Falls nicht: die URL aus der Adresszeile (mit `?code=...`) kopieren und im WSL-Terminal mit `curl "<URL>"` aufrufen.

> ⏳ Im Testing-Modus läuft der Refresh Token nach **7 Tagen** ab. Dann `node get-token.mjs` erneut ausführen.

## 4. Endpunkte abfragen

```bash
node explore.mjs          # alle Endpunkte, letzte 7 Tage
node explore.mjs sleep    # nur Endpunkte mit «sleep» im Namen
```

Ausgabe (Beispiel):

```
✓ identity                     200  pages=1 items=0  keys: ...
✓ steps-daily                  200  pages=1 items=7  keys: ...
✗ resting-heart-rate-daily     403  pages=1 items=0  keys: error
    → <Fehlermeldung von Google>
```

Zeitraum ändern: `FROM_DATE` und `TO_DATE` in `.env` setzen (`yyyy-MM-dd`).

## 5. Payloads anschauen

Jede Datei in `samples/` hat dieselbe Struktur:

```json
{
  "fetchedAt": "2026-10-21T12:20:22.513Z",
  "range": { "from": "2026-10-14", "to": "2026-10-21" },
  "request": { "method": "GET", "path": "...", "filter": "...", "body": null },
  "status": 200,
  "pages": [ { ...rohe Antwort von Google, Seite 1... } ]
}
```

### Was passiert bei mehreren Durchläufen?

- Pro Endpunkt gibt es **genau eine Datei** (`samples/<name>.json`). Ein neuer erfolgreicher Durchlauf **überschreibt** sie. Es entstehen keine Duplikate.
- Schlägt ein Request fehl (z. B. Token abgelaufen, `403`), wird die Antwort in `samples/<name>.error.json` gespeichert. Die letzte gute Datei bleibt erhalten. Beim nächsten erfolgreichen Durchlauf wird die `.error.json` gelöscht.
- `node explore.mjs sleep` aktualisiert nur die Dateien mit «sleep» im Namen. Die anderen bleiben vom letzten Durchlauf. `fetchedAt` und `range` zeigen, von wann und für welchen Zeitraum eine Datei ist.
- Die Dateien sind nur Anschauungsmaterial. Das Zusammenführen und Entfernen doppelter Tage über mehrere Abrufe hinweg gehört ins Backend (z. B. ein Eintrag pro Datum, der beim erneuten Import aktualisiert wird).

### Erkenntnisse aus dem ersten Abruf (Oktober 2026)

- **steps-daily**: ein Eintrag pro Tag, Datum in `civilStartTime.date`, Summe in `steps.countSum`. Der Wert ist ein **String** (`"20988"`).
- **sleep-reconcile**: pro Nacht `sleep.summary` mit `minutesAsleep`, `minutesAwake` und `stagesSummary` (AWAKE, LIGHT, DEEP, REM). **Einen Schlaf-Score (0–100) liefert die API nicht.** Den gibt es nur im Export (`Sleep Score/sleep_score.csv`, siehe [`../IMPORT_FORMAT.md`](../IMPORT_FORMAT.md)).
- **exercise-reconcile**: `exerciseType` (z. B. `WALKING`), `displayName`, `activeDuration` als String in Sekunden (`"1230s"`), dazu `metricsSummary` (Schritte, kcal, Distanz).
- **Zeitangaben**: `startTime`/`endTime` in UTC (`Z`), daneben `startUtcOffset` (`"7200s"` = +2 h). Für das lokale Datum muss der Offset dazugerechnet werden.
- Der Datumsfilter für `sleep` wird abgelehnt. Das Skript holt dann alle Nächte ohne Filter, deshalb muss im Backend selbst nach Datum gefiltert werden.
- `paired-devices` liefert `403 insufficient scopes`, weil dafür ein weiterer Scope nötig wäre. Für das Level-System wird der Endpunkt nicht gebraucht.

## Häufige Fehler

| Fehler | Ursache / Lösung |
|---|---|
| «Zugriff blockiert … Überprüfung nicht abgeschlossen» / `access_denied` | Eigene Adresse fehlt unter *Zielgruppe → Testnutzer*. Gilt auch für das Konto, dem das Projekt gehört |
| «Sign-in failed: no code» | `localhost:8765/callback` wurde ohne Antwort von Google geöffnet (z. B. von Hand oder neu geladen). `get-token.mjs` neu starten und die **neue** URL verwenden, am besten im privaten Fenster |
| `redirect_uri_mismatch` | `http://localhost:8765/callback` fehlt beim Webanwendungs-Client unter *Autorisierte Weiterleitungs-URIs*. Nach dem Speichern ein paar Minuten warten |
| `invalid_grant` | Refresh Token abgelaufen (7 Tage) oder widerrufen → `node get-token.mjs` |
| `403 ... has not been used / is disabled` | Google Health API im Projekt nicht aktiviert (Schritt 1.1) |
| `403 insufficient scopes` | Scope fehlt im Consent Screen oder wurde beim Anmelden nicht erlaubt |
| `400` bei einem Filter | Das Skript wiederholt den Request automatisch ohne Filter |
| leere `dataPoints` | Die Google Health App hat noch nicht synchronisiert, oder es gibt keine Daten im Zeitraum |

## Alternative ohne Skript: OAuth Playground

<https://developers.google.com/oauthplayground>
→ Zahnrad: *Use your own OAuth credentials* anhaken, Client ID/Secret eintragen
(beim OAuth-Client muss dann `https://developers.google.com/oauthplayground` als Redirect URI eingetragen sein, das geht nur mit dem Typ «Web application»)
→ Scopes einfügen → *Authorize APIs* → *Exchange authorization code for tokens* → Requests direkt im Playground ausführen.
