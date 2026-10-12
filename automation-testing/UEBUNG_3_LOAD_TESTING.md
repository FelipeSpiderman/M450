# Übung 3: Backend Last- und Performance-Testing (Traffic & Stress Testing)

## 1. Zielsetzung
Untersuchung und Belastung des Spring Boot Backends unter hohem Datenverkehr (Traffic/Concurrency). Evaluierung der Performance-Charakteristika, Durchsatzraten (Requests pro Sekunde) sowie Latenzverteilung (Percentiles) bei Lese- (`GET /students`) und Schreibzugriffen (`POST /students`).

---

## 2. Eingesetzte Testing Tools & Funktionalitäten

| Tool | Typ / Interface | Stärken | Typischer Einsatzbereich |
|------|-----------------|---------|--------------------------|
| **ApacheBench (`ab`)** | CLI Tool | Extrem schlank, standardmässig auf Unix/macOS vorinstalliert, minimale CPU-Overheads beim Erzeugen von Traffic | Schnelle Baseline-Messungen für statische oder REST-Endpunkte |
| **Autocannon** | Node.js High-Performance Tool | Unterstützt HTTP/1.1 Pipelining, Verbindungs-Pooling, 100k+ req/s, detaillierte Latenz-Percentile (p50, p90, p99) | Lokale Stresstests & Microbenchmarks |
| **Postman Collection Runner** | GUI & Newman CLI | Einfache Kombination von funktionalen API-Tests mit Wiederholungszyklen und Iterationen | Multi-User Workflow-Tests und funktionale Sequenzen |
| **Apache JMeter** | Java GUI / Headless CLI | Umfassende Protokollunterstützung, Thread Groups, Timer, Ramp-up Profile, Assertion-Sammlungen | Komplexe Unternehmens-Lasttests |

---

## 3. Durchgeführte Lasttests & Messwerte

> Abschnitt 3 enthält die erste Messung. Zu ihr gibt es keine Rohdaten. Die nachvollziehbare Nachmessung mit
> gespeicherter Ausgabe der Tools steht in **Abschnitt 4**.

### Testumgebung (erste Messung):
- **Backend:** Spring Boot 3.1.2 auf Embedded Apache Tomcat (Port 8081)
- **Persistenz:** In-Memory H2 Database mit HikariCP Connection Pool
- **Hardware:** Apple Silicon (arm64), Java 17

---

### Testfall A: Lesezugriff (`GET /students`)

#### 1. ApacheBench: 1'000 Requests mit 50 parallelen Verbindungen
```bash
ab -n 1000 -c 50 http://localhost:8081/students
```
**Messwerte:**
- **Gesamtdauer:** 0.044 Sekunden
- **Durchsatz (Throughput):** `22'859 Requests/Sekunde`
- **Mean Latenz (pro Request):** `2.187 ms`
- **Fehlerrate:** `0%` (0 Failed Requests)
- **Latenzverteilung:**
  - 50% (Median): `2 ms`
  - 95%: `3 ms`
  - 99%: `3 ms`
  - Max: `4 ms`

#### 2. Autocannon: 10 gleichzeitige Verbindungen über 5 Sekunden
```bash
npx autocannon -c 10 -d 5 --latency http://localhost:8081/students
```
**Messwerte:**
- **Gesamtzahl Requests:** `219'000 Requests`
- **Durchschnittlicher Durchsatz:** `43'440 Req/Sec`
- **Datenrate:** `21.5 MB/s`
- **Latenz:**
  - 50% (Median): `23 ms`
  - 90%: `43 ms`
  - 99%: `47 ms`
  - Max: `50 ms`

---

### Testfall B: Schreibzugriff mit JSON-Payload (`POST /students`)

Autocannon Stresstest mit 20 parallelen Workern und POST-Payload:
```bash
npx autocannon -m POST -H "Content-Type: application/json" \
  -b '{"name":"LoadUser","email":"load@tbz.ch","course":"Informatik"}' \
  -c 20 -d 5 --latency http://localhost:8081/students
```
**Messwerte:**
- **Gesamtzahl Requests:** `287'000 Requests` in 5.01 Sekunden
- **Durchschnittlicher Durchsatz:** `56'739 Req/Sec`
- **Median Latenz (p50):** `24 ms`
- **p99 Latenz:** `62 ms`
- **Max Latenz:** `78 ms`

---

## 4. Nachmessung mit Rohdaten (12.10.2026)

Die Ausgabe der Tools ist unverändert gespeichert in [testing/load-results/](testing/load-results).

### Testumgebung
- **Hardware:** Intel Core i7-1370P (20 Threads), Windows mit WSL2
- **Backend:** Spring Boot 3.1.2, OpenJDK 17.0.20 im Docker-Container `eclipse-temurin:17-jdk` (`./mvnw spring-boot:run`)
- **Lastgenerator:** läuft im gleichen Netzwerk-Namespace wie das Backend (`docker run --network container:<backend>`),
  also auf derselben Maschine. Last erzeugen und Requests bearbeiten konkurrieren damit um dieselbe CPU.
- **Startzustand:** frisch gestartetes Backend mit 5 Studenten

| Test | Befehl | Requests | Durchsatz | Latenz p50 / p99 / max | Fehler | Rohdaten |
|---|---|---|---|---|---|---|
| A1 GET (kalte JVM) | `ab -n 1000 -c 50` | 1'000 | **3'225 req/s** | 13 / 44 / 62 ms | 0 | [A1-ab-get.txt](testing/load-results/A1-ab-get.txt) |
| A2 GET | `autocannon -c 10 -d 5` | 50k | **9'988 req/s** (avg) | 25 / 53 / 72 ms | 0 | [A2-autocannon-get.txt](testing/load-results/A2-autocannon-get.txt) |
| B POST | `autocannon -m POST ... -c 20 -d 5` | 65k | **12'930 req/s** (avg) | 28 / 58 / 66 ms | 0 | [B-autocannon-post.txt](testing/load-results/B-autocannon-post.txt) |
| C GET nach Test B | `ab -n 100 -c 10` | 100 | **28 req/s** | 310 / 588 ms | 0 | [C-ab-get-after-post.txt](testing/load-results/C-ab-get-after-post.txt) |

ApacheBench wurde mit dem Image `httpd:2.4` ausgeführt, autocannon 8 mit `node:22` (`npx autocannon@8`).

### Beobachtungen
- **Warm-up:** Der erste Lauf (A1) ist deutlich langsamer als die folgenden. Die JVM optimiert häufig benutzten
  Code erst zur Laufzeit (JIT). Für realistische Werte sollte man vor der eigentlichen Messung einen
  Aufwärm-Durchlauf machen.
- **Latenz und Durchsatz:** Bei autocannon passen Median-Latenz und Durchsatz nicht zusammen. Rechnet man
  10 Verbindungen bei 25 ms, ergäben sich etwa 400 req/s, gemessen wurden aber etwa 10'000. autocannon misst die
  Latenz nicht pro Verbindung und Request so, wie die einfache Rechnung annimmt. Die Latenzwerte sind deshalb nur
  untereinander vergleichbar, nicht als absolute Antwortzeit.
- **Wichtigster Befund: `GET /students` hat keine Paginierung.** Der POST-Test hat 64'660 Studenten angelegt.
  Danach liefert jeder GET alle Datensätze als **4,8 MB JSON**, und der Durchsatz fällt von etwa 10'000 auf
  **28 req/s**, also um mehr als das 300-fache. Ein Lasttest mit Schreibzugriffen verändert die Daten, die
  folgende Tests sehen. In einer echten Anwendung müsste die Liste paginiert werden (z.B. `Pageable` in Spring Data).
- Unterschiede zur ersten Messung (Abschnitt 3) sind zu erwarten: Dort lief alles direkt auf dem Rechner
  (Apple Silicon), hier im Docker-Container unter WSL2.

---

## 5. Analyse und Erkenntnisse

1. **Hoher Durchsatz bei In-Memory-Datenbank:**
   Da die Anwendung eine H2 In-Memory-Datenbank mit HikariCP nutzt, reagiert das Backend extrem schnell (Durchsätze von >20'000 bis ~56'000 Requests/Sekunde).
2. **Latenzstabilität unter Last:**
   Selbst unter hoher Last mit zehntausenden Anfragen pro Sekunde stieg die Latenz (p99) nicht über 65 ms.
3. **Mögliche Bottlenecks in Produktivumgebungen:**
   - **Datenbankverbindungen:** Standardmässig begrenzt HikariCP den Pool auf 10 Verbindungen. Bei realen relationalen Datenbanken über das Netzwerk (z. B. PostgreSQL/MySQL) würde der Pool zum Flaschenhals werden.
   - **Tomcat Worker Threads:** Standardmässig 200 Threads (`server.tomcat.threads.max`). Bei langsamen I/O-Operationen würden die Worker blockieren (Lösung: Virtual Threads / Project Loom oder WebFlux).
   - **Memory & Garbage Collection:** Bei Millionen von erzeugten Objekten (DTOs, JSON-Maps) steigt die GC-Aktivität.
