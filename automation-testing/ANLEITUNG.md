# Anleitung Automation Testing (für mich)

Was am 12.10. neu dazugekommen ist und wie ich es laufen lasse.

## 1. Backend starten

Mit Java 17 oder neuer:

```bash
cd automation-testing/spring-boot-angular-basic-lw2
./mvnw spring-boot:run
```

Ohne Java, mit Docker:

```bash
cd automation-testing/spring-boot-angular-basic-lw2
docker run -d --rm --name student-backend -p 8081:8081 \
  -u $(id -u):$(id -g) -e HOME=/tmp -v "$PWD":/app -w /app \
  eclipse-temurin:17-jdk ./mvnw spring-boot:run
```

Läuft, wenn http://localhost:8081/students eine JSON-Liste mit 5 Studenten zeigt. Beim ersten Mal dauert es ein
paar Minuten, weil Maven alles herunterlädt. Stoppen mit `docker stop student-backend`.

**Warum `./mvnw` vorher nicht ging:** Das Skript liest `.mvn/wrapper/maven-wrapper.properties`, und genau dieser
Ordner hat im Repo gefehlt. Jetzt ist der Wrapper 3.3.4 drin, der lädt Maven selbst herunter und braucht keine
`.jar`-Datei.

## 1.1. Frontend starten (Angular GUI)

Das Angular-Frontend liegt im Unterordner `spring-boot-angular-basic-lw2/src/main/js/my-app`.

```bash
cd automation-testing/spring-boot-angular-basic-lw2/src/main/js/my-app
npm install  # bzw. npm ci
npm start    # oder: npx ng serve
```

Die Anwendung ist anschliessend unter **http://localhost:4200** im Browser erreichbar und kommuniziert mit dem Backend auf Port 8081.

## 2. Backend-Tests (Übung 1)

```bash
cd automation-testing/spring-boot-angular-basic-lw2
./mvnw test
```

Erwartet: `Tests run: 6, Failures: 0`.

## 3. E2E-Tests mit Playwright (Übung 2)

```bash
cd automation-testing/spring-boot-angular-basic-lw2/src/main/js/my-app
npm ci
npx playwright install chromium
npm run test:e2e
npx playwright show-report
```

Falls Chromium mit `libnspr4.so: cannot open shared object file` abbricht, fehlen System-Bibliotheken
(`sudo npx playwright install-deps`). Oder ohne sudo über Docker (das Backend muss dafür laufen):

```bash
docker run --rm --network host --ipc=host -u $(id -u):$(id -g) -e HOME=/tmp \
  -v "$PWD":/work -w /work mcr.microsoft.com/playwright:v1.63.0-noble npx playwright test
```

**Der neue Test `e2e/full-stack.spec.ts`:**
- Die alten Tests ersetzen das Backend mit `page.route(...)` durch Fake-Daten. Das ist stabil, testet aber nicht,
  ob Frontend und Backend wirklich zusammenspielen.
- Der neue Test füllt das Formular aus und klickt Submit. Danach prüft er:
  1. dass der POST mit Status 201 zurückkommt,
  2. dass der neue Student in der Tabelle steht,
  3. dass der Student auch direkt über die REST-API (`GET /students`) da ist.
- Der Name enthält `Date.now()`, damit er bei jedem Lauf eindeutig ist.
- Läuft das Backend nicht, wird der Test übersprungen (skipped) und nicht rot. Die Meldung sagt dann, was zu tun ist.

## 4. Lasttest (Übung 3)

Backend frisch starten, damit nur die 5 Startdaten drin sind. Dann:

### Automatisierter Test & Verifikation (All-in-One Skript)

Führt Verfügbarkeits-Check, GET-Benchmark, POST-Stresstest und abschliessende Funktionsprüfung automatisiert durch:

```bash
node testing/run-load-tests.js
```

### Manuelle Tests mit ApacheBench & Autocannon

```bash
# GET mit ApacheBench
docker run --rm --network host httpd:2.4 ab -n 1000 -c 50 http://localhost:8081/students

# GET mit autocannon
npx autocannon -c 10 -d 5 --latency http://localhost:8081/students

# POST mit autocannon
npx autocannon -m POST -H "Content-Type: application/json" \
  -b '{"name":"LoadUser","email":"load@tbz.ch","course":"Informatik"}' \
  -c 20 -d 5 --latency http://localhost:8081/students
```

Unter Docker Desktop + WSL war `localhost:8081` vom Host aus manchmal nicht erreichbar. Dann das Tool direkt im
Netzwerk des Backend-Containers starten: `--network container:student-backend` statt `--network host`.

Meine Messung liegt in `testing/load-results/`. Das sollte ich mir merken:
- Der **erste** Lauf ist langsamer (etwa 3'200 req/s), weil die JVM noch „kalt“ ist. Danach sind es etwa 10'000.
- **Nach dem POST-Test** liegen über 64'000 Studenten in der DB. Weil `GET /students` keine Paginierung hat, kommen
  dann pro Request 4,8 MB zurück, und es sind nur noch 28 req/s. Das ist der interessanteste Befund.
- Bei autocannon passen Latenz und Durchsatz rechnerisch nicht zusammen (10 Verbindungen × 25 ms wären etwa
  400 req/s). Das liegt daran, wie autocannon die Latenz misst. Die Werte sind darum nur untereinander vergleichbar.

## Wenn die Lehrperson fragt

- *Ist das wirklich End-to-End?* Ja, `full-stack.spec.ts` läuft ohne Mocks. Die anderen 4 Tests mocken das Backend
  absichtlich, damit sie unabhängig vom Backend laufen.
- *Was ist der Flaschenhals?* Die fehlende Paginierung. Lösung wäre z.B. `Pageable` in Spring Data:
  `GET /students?page=0&size=20`.
- *Warum ist die erste Messung anders als die zweite?* Andere Hardware (Mac vs. WSL/Docker) und JVM-Warm-up.
