# Health Leveling – Frontend

Angular 21 (Standalone Components, SCSS). Unit Tests laufen mit Vitest, E2E-Tests mit Playwright.

## Starten

```bash
npm install
npm start            # http://localhost:4200
```

Anfragen an `/api` werden über `proxy.conf.json` an das Backend (`http://localhost:8080`) weitergeleitet.

## Tests

```bash
npm test -- --watch=false     # Unit Tests (Vitest)
npm run build                 # Production Build

# E2E (einmalig: Browser + Systembibliotheken installieren)
npx playwright install chromium
sudo npx playwright install-deps chromium
npm run test:e2e              # startet ng serve automatisch
E2E_PORT=4300 npm run test:e2e  # falls Port 4200 schon belegt ist
npx playwright show-report    # HTML-Report
```

## Idee und Vorgehen des Frontends

### Ziel

Das Frontend soll eine selbsterklärende und einfach bedienbare Oberfläche bieten. Die Benutzerin oder der Benutzer soll ohne Anleitung verstehen, wo welche Daten erfasst werden und wie sich das eigene Level entwickelt.

### Funktionen

- **Profil:** Anzeige des eigenen Profils mit aktuellem Level, XP und Fortschrittsbalken.
- **Einträge erfassen:** Aktivitäten, Mahlzeiten, Wasser und Gewicht können über Formulare erfasst werden. Dies ist die wichtigste Funktion des Frontends.

### Bewusst nicht umgesetzt

Ursprünglich war angedacht, auch die Profile anderer Personen anzuzeigen. Da jedoch nur ein einziges Google-Health-Konto mit der Applikation verbunden ist, ergibt diese Funktion keinen Sinn und wird daher nicht umgesetzt.

### Validierung

Alle Eingaben werden bereits im Frontend validiert, bevor sie an das Backend gesendet werden. Dies hat zwei Vorteile:

1. Die Benutzerin oder der Benutzer erhält sofort eine verständliche Rückmeldung, was falsch eingegeben wurde und wie der Fehler behoben werden kann.
2. Ungültige Einträge erreichen das Backend gar nicht erst.

Das Backend validiert die Daten trotzdem ein zweites Mal, da es sich nicht allein auf das Frontend verlassen darf.

## Umsetzung

### Seiten

| Seite | Pfad | Inhalt |
|---|---|---|
| Profil | `/` | Level, XP, Fortschrittsbalken, Schritte-Serie und Achievements |
| Erfassen | `/erfassen` | Formular für Aktivität, Mahlzeit, Getränk und Gewicht, Liste der Einträge eines Tages |
| Tage | `/tage` | Schritte und Schlaf-Score manuell erfassen, Übersicht aller Tage |
| Import | `/import` | CSV- oder JSON-Datei importieren, Google Health synchronisieren |

### Aufbau

```
src/app/
├── core/            ApiService (einziger Zugriff aufs Backend), Modelle, Validatoren, Fehlermeldungen
├── profile/         Profil-Seite
├── entries/         Erfassen-Seite
├── days/            Tage-Seite
└── import/          Import-Seite
```

Jede Seite ist eine Standalone-Komponente mit Signals für den Zustand und Reactive Forms für die Eingaben. Das Styling liegt gesammelt in `src/styles.scss`.

### Unabhängig vom Backend

- Alle Anfragen laufen über den `ApiService`. Nur dort stehen die URLs.
- Ist das Backend nicht erreichbar, zeigt jede Seite eine verständliche Meldung statt eines Fehlers.
- Die Unit Tests ersetzen den `ApiService` durch Mocks bzw. das HTTP durch `HttpTestingController`.
- Die E2E-Tests beantworten alle `/api`-Anfragen direkt im Browser (`page.route`). Es braucht also kein laufendes Backend und keine echten Daten.

### Validierung

Die Grenzen sind dieselben wie im Backend:

| Feld | Regel |
|---|---|
| Zeitpunkt / Datum | Pflicht, nicht in der Zukunft |
| Aktivität | Bezeichnung und Dauer (1–1440 Min.) Pflicht, Kalorien optional (0–5000) |
| Mahlzeit | Kalorien Pflicht (0–5000) |
| Getränk | Menge Pflicht (1–5000 ml) |
| Gewicht | Pflicht (20–400 kg) |
| Tageswerte | Schritte (0–100'000) oder Schlaf-Score (0–100), mindestens eines davon |

