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
