// Calls the Google Health API endpoints relevant for the level system
// and saves every raw response to samples/<name>.json.
// Usage: node explore.mjs            (all endpoints)
//        node explore.mjs sleep      (only endpoints whose name contains "sleep")
import { mkdirSync, writeFileSync } from 'node:fs';
import { fileURLToPath } from 'node:url';
import { loadEnv, requireValue } from './env.mjs';

const BASE_URL = 'https://health.googleapis.com/v4/users/me';
const SAMPLES_DIR = fileURLToPath(new URL('samples/', import.meta.url));
const MAX_PAGES = 3;
const MAX_RETRIES = 4;

const env = loadEnv();
const toDate = env.TO_DATE || isoDate(new Date());
const fromDate = env.FROM_DATE || isoDate(new Date(Date.now() - 7 * 86400000));

// filter: interval types filter on civil (local) start time, see ENDPOINTS.md §5.
// If Google rejects a filter (400), the request is repeated without it.
const ENDPOINTS = [
  { name: 'identity', method: 'GET', path: '/identity' },
  { name: 'paired-devices', method: 'GET', path: '/pairedDevices' },
  { name: 'steps-daily', method: 'POST', path: '/dataTypes/steps/dataPoints:dailyRollUp', body: dailyRollUpBody() },
  { name: 'steps-reconcile', method: 'GET', path: '/dataTypes/steps/dataPoints:reconcile', filter: `steps.interval.civil_start_time >= "${fromDate}T00:00:00"`, maxPages: 1 },
  { name: 'sleep-reconcile', method: 'GET', path: '/dataTypes/sleep/dataPoints:reconcile', filter: `sleep.interval.civil_start_time >= "${fromDate}T00:00:00"` },
  { name: 'exercise-reconcile', method: 'GET', path: '/dataTypes/exercise/dataPoints:reconcile', filter: `exercise.interval.civil_start_time >= "${fromDate}T00:00:00"` },
  { name: 'active-zone-minutes-daily', method: 'POST', path: '/dataTypes/active-zone-minutes/dataPoints:dailyRollUp', body: dailyRollUpBody() },
  { name: 'resting-heart-rate-daily', method: 'GET', path: '/dataTypes/daily-resting-heart-rate/dataPoints:reconcile' },
];

const only = process.argv[2];
const selected = only ? ENDPOINTS.filter((e) => e.name.includes(only)) : ENDPOINTS;

mkdirSync(SAMPLES_DIR, { recursive: true });
console.log(`Range: ${fromDate} → ${toDate}\n`);

const accessToken = await getAccessToken();
for (const endpoint of selected) {
  await explore(endpoint);
}
console.log(`\nRaw responses saved in ${SAMPLES_DIR}`);

async function getAccessToken() {
  const response = await fetch('https://oauth2.googleapis.com/token', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: new URLSearchParams({
      client_id: requireValue(env, 'GOOGLE_CLIENT_ID'),
      client_secret: requireValue(env, 'GOOGLE_CLIENT_SECRET'),
      refresh_token: requireValue(env, 'GOOGLE_REFRESH_TOKEN'),
      grant_type: 'refresh_token',
    }),
  });
  const tokens = await response.json();
  if (!response.ok) {
    console.error('Could not refresh the access token:', tokens);
    console.error('If the error is "invalid_grant", the refresh token expired (7 days in Testing mode): run node get-token.mjs again.');
    process.exit(1);
  }
  return tokens.access_token;
}

async function explore(endpoint) {
  let filter = endpoint.filter;
  const pages = [];
  let pageToken;

  for (let page = 0; page < (endpoint.maxPages ?? MAX_PAGES); page++) {
    const url = new URL(BASE_URL + endpoint.path);
    if (filter) url.searchParams.set('filter', filter);
    if (pageToken) url.searchParams.set('pageToken', pageToken);

    const { status, data } = await request(endpoint.method, url, endpoint.body);

    if (status === 400 && filter) {
      console.log(`  ${endpoint.name}: filter rejected (${data?.error?.message ?? 'no message'}), retrying without filter`);
      filter = undefined;
      page--;
      continue;
    }

    pages.push({ status, data });
    if (status !== 200) break;
    pageToken = data.nextPageToken;
    if (!pageToken) break;
  }

  const last = pages.at(-1);
  const result = {
    request: { method: endpoint.method, path: endpoint.path, filter: filter ?? null, body: endpoint.body ?? null },
    status: last.status,
    pages: pages.map((p) => p.data),
  };
  writeFileSync(`${SAMPLES_DIR}${endpoint.name}.json`, JSON.stringify(result, null, 2));

  const keys = last.data && typeof last.data === 'object' ? Object.keys(last.data).join(', ') : '-';
  const count = pages.reduce((sum, p) => sum + countItems(p.data), 0);
  const mark = last.status === 200 ? '✓' : '✗';
  console.log(`${mark} ${endpoint.name.padEnd(28)} ${last.status}  pages=${pages.length} items=${count}  keys: ${keys}`);
  if (last.status !== 200) console.log(`    → ${last.data?.error?.message ?? JSON.stringify(last.data)}`);
}

async function request(method, url, body) {
  for (let attempt = 0; ; attempt++) {
    const response = await fetch(url, {
      method,
      headers: {
        Authorization: `Bearer ${accessToken}`,
        Accept: 'application/json',
        ...(body && { 'Content-Type': 'application/json' }),
      },
      body: body && JSON.stringify(body),
    });

    if ((response.status === 429 || response.status === 504) && attempt < MAX_RETRIES) {
      const waitMs = 2 ** attempt * 1000;
      console.log(`  ${response.status}, retrying in ${waitMs / 1000}s ...`);
      await new Promise((resolve) => setTimeout(resolve, waitMs));
      continue;
    }

    const text = await response.text();
    let data;
    try {
      data = JSON.parse(text);
    } catch {
      data = { raw: text };
    }
    return { status: response.status, data };
  }
}

// Counts the entries of the first array in the response (dataPoints, rollUpDataPoints, ...)
function countItems(data) {
  if (!data || typeof data !== 'object') return 0;
  const firstArray = Object.values(data).find(Array.isArray);
  return firstArray ? firstArray.length : 0;
}

function dailyRollUpBody() {
  const civilDate = (iso) => {
    const [year, month, day] = iso.split('-').map(Number);
    return { date: { year, month, day }, time: {} };
  };
  return { range: { start: civilDate(fromDate), end: civilDate(toDate) }, windowSizeDays: 1 };
}

function isoDate(date) {
  return date.toLocaleDateString('sv-SE'); // yyyy-MM-dd in local time
}
