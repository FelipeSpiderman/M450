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
