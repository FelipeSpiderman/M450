import { expect, Page, Route, test } from '@playwright/test';

// The backend is not needed: every /api request is answered here in the browser.
// That way the E2E tests only test the frontend and never depend on real data.

const profile = {
  level: 2,
  totalXp: 150,
  xpIntoLevel: 50,
  xpForNextLevel: 200,
  progressPercent: 25,
  currentStreak: 3,
};

const achievements = [
  { achievement: 'FIRST_ENTRY', title: 'First step', description: 'Log your first entry', unlocked: true },
  { achievement: 'LEVEL_5', title: 'Rising star', description: 'Reach level 5', unlocked: false },
];

/** Fake backend. Returns the bodies of all POST requests so the tests can check them. */
async function mockApi(page: Page): Promise<unknown[]> {
  const posted: unknown[] = [];
  const entries: unknown[] = [];

  await page.route('**/api/**', async (route: Route) => {
    const request = route.request();
    const path = new URL(request.url()).pathname;

    const isJson = request.headers()['content-type']?.includes('application/json');
    if (request.method() === 'POST') {
      posted.push(isJson ? request.postDataJSON() : request.postData());
    }
    if (path === '/api/profile') return route.fulfill({ json: profile });
    if (path === '/api/achievements') return route.fulfill({ json: achievements });
    if (path === '/api/days') return route.fulfill({ json: [] });
    if (path === '/api/entries' && request.method() === 'GET') return route.fulfill({ json: entries });
    if (path === '/api/entries') {
      const entry = { ...request.postDataJSON(), id: entries.length + 1, xp: 40 };
      entries.push(entry);
      return route.fulfill({ status: 201, json: entry });
    }
    if (path === '/api/import') return route.fulfill({ json: { created: 2, updated: 0 } });
    return route.fulfill({ status: 404, json: { detail: 'Not mocked: ' + path } });
  });
  return posted;
}

test('profile shows level, progress and achievements', async ({ page }) => {
  await mockApi(page);
  await page.goto('/');

  await expect(page).toHaveTitle(/Health Leveling/);
  await expect(page.getByText('Level 2', { exact: true })).toBeVisible();
  await expect(page.getByRole('progressbar')).toHaveAttribute('aria-valuenow', '25');
  await expect(page.getByText('3 Tage')).toBeVisible();
  await expect(page.getByText('First step')).toBeVisible();
});

test('navigation leads to all pages', async ({ page }) => {
  await mockApi(page);
  await page.goto('/');

  for (const [link, heading] of [
    ['Erfassen', 'Erfassen'],
    ['Tage', 'Tage'],
    ['Import', 'Import'],
    ['Profil', 'Profil'],
  ]) {
    await page.getByRole('link', { name: link }).click();
    await expect(page.getByRole('heading', { level: 1 })).toHaveText(heading);
  }
});

test('an activity can be logged and appears in the list', async ({ page }) => {
  const posted = await mockApi(page);
  await page.goto('/erfassen');

  await page.getByLabel('Bezeichnung').fill('Laufen');
  await page.getByLabel('Dauer in Minuten').fill('30');
  await page.getByRole('button', { name: 'Speichern' }).click();

  await expect(page.getByRole('status')).toHaveText('Aktivität gespeichert: +40 XP');
  await expect(page.getByText('Laufen, 30 Min.')).toBeVisible();
  expect(posted[0]).toMatchObject({ type: 'ACTIVITY', name: 'Laufen', durationMinutes: 30, calories: null });
});

test('invalid input is explained and not sent', async ({ page }) => {
  const posted = await mockApi(page);
  await page.goto('/erfassen');

  await page.getByLabel('Art').selectOption({ label: 'Gewicht' });
  await page.getByLabel('Gewicht in kg').fill('500');
  await page.getByRole('button', { name: 'Speichern' }).click();

  await expect(page.getByText('Höchstens 400.')).toBeVisible();
  expect(posted).toHaveLength(0);
});

test('a CSV file can be imported', async ({ page }) => {
  await mockApi(page);
  await page.goto('/import');

  await page.getByLabel('Datei').setInputFiles({
    name: 'export.csv',
    mimeType: 'text/csv',
    buffer: Buffer.from('date,steps,sleep_score,activity_count,activity_minutes\n2026-10-06,8412,72,1,69\n'),
  });
  await page.getByRole('button', { name: 'Importieren' }).click();

  await expect(page.getByRole('status')).toContainText('2 neue Tage');
});

test('a clear message is shown when the backend is down', async ({ page }) => {
  await page.route('**/api/**', (route) => route.abort());
  await page.goto('/');

  await expect(page.getByRole('alert')).toContainText('Backend ist nicht erreichbar');
});
