#!/usr/bin/env node

// Einfacher Lasttest für Übung 3 (M450)
// Testet GET und POST auf /students und prüft, ob das Backend stabil bleibt.

const http = require('http');

const BASE_URL = process.env.BASE_URL || 'http://localhost:8081';

// Kleiner Helper für HTTP-Requests
function doRequest(path, method = 'GET', data = null) {
  return new Promise((resolve) => {
    const start = Date.now();
    const url = new URL(path, BASE_URL);

    const req = http.request(
      url,
      {
        method,
        headers: data ? { 'Content-Type': 'application/json' } : {},
      },
      (res) => {
        let body = '';
        res.on('data', (chunk) => { body += chunk; });
        res.on('end', () => {
          resolve({
            ok: res.statusCode >= 200 && res.statusCode < 300,
            status: res.statusCode,
            time: Date.now() - start,
            body,
          });
        });
      }
    );

    req.on('error', (err) => {
      resolve({
        ok: false,
        status: 0,
        time: Date.now() - start,
        error: err.message,
      });
    });

    if (data) req.write(JSON.stringify(data));
    req.end();
  });
}

// Führt N Requests mit bestimmter Concurrency aus
async function runLoad(name, total, concurrency, createRequest) {
  console.log(`\n-> ${name} (${total} Requests, Concurrency: ${concurrency})`);

  let count = 0;
  let success = 0;
  let failed = 0;
  let totalTime = 0;
  const start = Date.now();

  async function worker() {
    while (count < total) {
      const idx = count++;
      const req = createRequest(idx);
      const res = await doRequest(req.path, req.method, req.data);

      totalTime += res.time;
      if (res.ok) {
        success++;
      } else {
        failed++;
      }
    }
  }

  const workers = [];
  for (let i = 0; i < concurrency; i++) {
    workers.push(worker());
  }
  await Promise.all(workers);

  const durationSec = (Date.now() - start) / 1000;
  const rps = Math.round(total / durationSec);
  const avgLatency = Math.round(totalTime / total);

  console.log(`   Dauer:        ${durationSec.toFixed(2)}s`);
  console.log(`   Durchsatz:    ~${rps} req/s`);
  console.log(`   Erfolgreich:  ${success}/${total} (${failed} Fehler)`);
  console.log(`   Avg Latenz:   ${avgLatency} ms`);
}

async function main() {
  console.log(`=== Lasttest Start (Target: ${BASE_URL}) ===`);

  // 1. Checken ob Server erreichbar ist
  console.log('\n[1/4] Verbindung zum Backend testen...');
  const ping = await doRequest('/students');
  if (!ping.ok) {
    console.error(`Backend nicht erreichbar unter ${BASE_URL}!`);
    console.error('Bitte zuerst Backend starten: ./mvnw spring-boot:run');
    process.exit(1);
  }
  console.log(`Backend antwortet (HTTP ${ping.status}).`);

  // 2. GET Lasttest
  await runLoad('GET /students (Lesen)', 1000, 20, () => ({
    path: '/students',
    method: 'GET',
  }));

  // 3. POST Lasttest (Studenten anlegen)
  await runLoad('POST /students (Schreiben)', 300, 15, (i) => ({
    path: '/students',
    method: 'POST',
    data: {
      name: `TestStudent_${i}`,
      email: `test_${i}_${Date.now()}@tbz.ch`,
      course: 'Informatik',
    },
  }));

  // 4. Nachkontrolle
  console.log('\n[4/4] Funktionspruefung nach dem Lasttest...');
  const check = await doRequest('/students');
  if (check.ok) {
    try {
      const list = JSON.parse(check.body);
      console.log(`Alles gut! Backend laeuft stabil und enthaelt ${list.length} Studenten.`);
    } catch {
      console.log('Backend antwortet wieder normal.');
    }
  } else {
    console.error('Backend antwortet nach dem Lasttest mit Fehlern!');
    process.exit(1);
  }

  console.log('\n=== Test abgeschlossen ===');
}

main().catch((err) => {
  console.error('Fehler:', err);
  process.exit(1);
});
