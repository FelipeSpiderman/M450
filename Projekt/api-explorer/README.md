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
   - Application type: **Desktop app**. Bei diesem Typ ist `http://localhost` als Redirect automatisch erlaubt, man muss nichts eintragen.
   - Client ID und Client Secret kopieren.

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
  "request": { "method": "GET", "path": "...", "filter": "...", "body": null },
  "status": 200,
  "pages": [ { ...rohe Antwort von Google, Seite 1... } ]
}
```

Worauf achten:
- **steps-daily**: Wie sieht ein Tageswert aus, wo steht die Summe?
- **sleep-reconcile**: Gibt es einen Schlaf-*Score* (0–100) oder nur Phasen und Dauer? Im Export steht der Score in `Sleep Score/sleep_score.csv` (siehe [`../IMPORT_FORMAT.md`](../IMPORT_FORMAT.md)).
- **exercise-reconcile**: Name der Aktivität, Start, Dauer.
- Zeitangaben: UTC (`physicalTime`) oder lokal (`civilTime`)?

## Häufige Fehler

| Fehler | Ursache / Lösung |
|---|---|
| `access_denied` beim Anmelden | Eigene Adresse fehlt unter *Test users* |
| `redirect_uri_mismatch` | OAuth-Client ist vom Typ «Web application» statt «Desktop app». Entweder neuen Desktop-Client erstellen oder `http://localhost:8765/callback` als Redirect URI eintragen |
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
