// Tiny .env reader/writer, so the explorer needs no npm packages.
import { readFileSync, writeFileSync, existsSync } from 'node:fs';
import { fileURLToPath } from 'node:url';

export const ENV_PATH = fileURLToPath(new URL('.env', import.meta.url));

export function loadEnv() {
  if (!existsSync(ENV_PATH)) {
    console.error('No .env found. Run: cp .env.example .env  and fill in GOOGLE_CLIENT_ID / GOOGLE_CLIENT_SECRET.');
    process.exit(1);
  }
  const env = {};
  for (const line of readFileSync(ENV_PATH, 'utf8').split('\n')) {
    const match = line.match(/^\s*([A-Z_]+)\s*=\s*(.*)\s*$/);
    if (match) env[match[1]] = match[2];
  }
  return env;
}

export function saveEnvValue(key, value) {
  const lines = readFileSync(ENV_PATH, 'utf8').split('\n');
  const index = lines.findIndex((line) => line.startsWith(`${key}=`));
  if (index >= 0) lines[index] = `${key}=${value}`;
  else lines.push(`${key}=${value}`);
  writeFileSync(ENV_PATH, lines.join('\n'));
}

export function requireValue(env, key) {
  if (!env[key]) {
    console.error(`${key} is missing in .env`);
    process.exit(1);
  }
  return env[key];
}
