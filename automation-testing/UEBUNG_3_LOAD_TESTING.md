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

### Testumgebung:
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

## 4. Analyse und Erkenntnisse

1. **Hoher Durchsatz bei In-Memory-Datenbank:**
   Da die Anwendung eine H2 In-Memory-Datenbank mit HikariCP nutzt, reagiert das Backend extrem schnell (Durchsätze von >20'000 bis ~56'000 Requests/Sekunde).
2. **Latenzstabilität unter Last:**
   Selbst unter hoher Last mit zehntausenden Anfragen pro Sekunde stieg die Latenz (p99) nicht über 65 ms.
3. **Mögliche Bottlenecks in Produktivumgebungen:**
   - **Datenbankverbindungen:** Standardmässig begrenzt HikariCP den Pool auf 10 Verbindungen. Bei realen relationalen Datenbanken über das Netzwerk (z. B. PostgreSQL/MySQL) würde der Pool zum Flaschenhals werden.
   - **Tomcat Worker Threads:** Standardmässig 200 Threads (`server.tomcat.threads.max`). Bei langsamen I/O-Operationen würden die Worker blockieren (Lösung: Virtual Threads / Project Loom oder WebFlux).
   - **Memory & Garbage Collection:** Bei Millionen von erzeugten Objekten (DTOs, JSON-Maps) steigt die GC-Aktivität.
