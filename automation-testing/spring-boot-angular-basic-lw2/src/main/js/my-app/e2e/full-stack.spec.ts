import { test, expect } from '@playwright/test';

// Echter End-to-End-Test ohne page.route()-Mocks: Browser -> Angular -> Spring Boot -> H2.
// Das Backend muss laufen (im Projektordner: ./mvnw spring-boot:run, Port 8081).
const BACKEND_URL = 'http://localhost:8081/students';

test.describe('Full-Stack End-to-End Test (echtes Backend)', () => {

  test.beforeAll(async ({ request }) => {
    const backendUp = await request.get(BACKEND_URL).then(r => r.ok()).catch(() => false);
    test.skip(!backendUp, `Backend unter ${BACKEND_URL} nicht erreichbar - zuerst ./mvnw spring-boot:run starten`);
  });

  test('should persist a new student in the backend and show it in the list', async ({ page, request }) => {
    const uniqueName = `E2E Full Stack ${Date.now()}`;
    const email = `fullstack.${Date.now()}@tbz.ch`;

    await page.goto('/addstudents');
    await page.fill('#name', uniqueName);
    await page.fill('#email', email);
    await page.selectOption('#course', 'Informatik');

    const postResponse = page.waitForResponse(r => r.url() === BACKEND_URL && r.request().method() === 'POST');
    await page.click('button[type="submit"]');
    expect((await postResponse).status()).toBe(201);

    // GUI: Nach dem Speichern wird zur Liste navigiert, die Daten kommen vom echten Backend
    await expect(page).toHaveURL(/.*students/);
    const row = page.locator('table tbody tr', { hasText: uniqueName });
    await expect(row).toBeVisible();
    await expect(row).toContainText(email);
    await expect(row).toContainText('Informatik');

    // Backend: Der Student ist wirklich gespeichert
    const students = await (await request.get(BACKEND_URL)).json();
    expect(students.some((s: { name: string }) => s.name === uniqueName)).toBe(true);
  });
});
