import { test, expect } from '@playwright/test';

test.describe('Student Application End-to-End Tests', () => {

  test('should display home layout with navigation buttons and logo', async ({ page }) => {
    await page.goto('/');

    // Check page title and logo
    await expect(page.locator('img[src="./assets/tbz_logo.png"]')).toBeVisible();

    // Check navigation buttons
    const listButton = page.getByRole('link', { name: 'List Students' });
    const addButton = page.getByRole('link', { name: 'Add Students' });

    await expect(listButton).toBeVisible();
    await expect(addButton).toBeVisible();
  });

  test('should navigate to /students and render student list table with course column', async ({ page }) => {
    // Intercept backend API call with mock data for reliable isolated testing
    await page.route('http://localhost:8081/students', async route => {
      if (route.request().method() === 'GET') {
        const json = [
          { id: '1', name: 'Jonas', email: 'jonas@tbz.ch', course: 'Informatik' },
          { id: '2', name: 'Patrick', email: 'patrick@tbz.ch', course: 'Mediamatik' }
        ];
        await route.fulfill({ json });
      } else {
        await route.continue();
      }
    });

    await page.goto('/students');

    // Verify table headers
    await expect(page.locator('th', { hasText: 'Name' })).toBeVisible();
    await expect(page.locator('th', { hasText: 'Email' })).toBeVisible();
    await expect(page.locator('th', { hasText: 'Course' })).toBeVisible();

    // Verify student data rows
    await expect(page.locator('table tbody tr')).toHaveCount(2);
    await expect(page.locator('table tbody tr').first()).toContainText('Jonas');
    await expect(page.locator('table tbody tr').first()).toContainText('jonas@tbz.ch');
    await expect(page.locator('table tbody tr').first()).toContainText('Informatik');
  });

  test('should navigate to /addstudents, select course, and add a new student successfully', async ({ page }) => {
    let postRequestPayload: any = null;

    await page.route('http://localhost:8081/students', async route => {
      if (route.request().method() === 'POST') {
        postRequestPayload = JSON.parse(route.request().postData() || '{}');
        await route.fulfill({ status: 201, contentType: 'application/json', body: JSON.stringify({ id: 3, ...postRequestPayload }) });
      } else if (route.request().method() === 'GET') {
        await route.fulfill({
          json: [
            { id: '1', name: 'Jonas', email: 'jonas@tbz.ch', course: 'Informatik' },
            { id: '3', name: 'E2E Test Student', email: 'e2e@tbz.ch', course: 'Mediamatik' }
          ]
        });
      } else {
        await route.continue();
      }
    });

    await page.goto('/addstudents');

    // Fill in student form
    await page.fill('#name', 'E2E Test Student');
    await page.fill('#email', 'e2e@tbz.ch');
    await page.selectOption('#course', 'Mediamatik');

    // Submit form
    await page.click('button[type="submit"]');

    // Expect navigation to /students list
    await expect(page).toHaveURL(/.*students/);
    expect(postRequestPayload.name).toBe('E2E Test Student');
    expect(postRequestPayload.email).toBe('e2e@tbz.ch');
    expect(postRequestPayload.course).toBe('Mediamatik');

    // Verify table contains newly added student with course
    await expect(page.locator('table tbody')).toContainText('E2E Test Student');
    await expect(page.locator('table tbody')).toContainText('Mediamatik');
  });

  test('should validate form and disable submit when required fields are missing', async ({ page }) => {
    await page.goto('/addstudents');

    const submitBtn = page.locator('button[type="submit"]');
    // Initially when empty, submit is disabled
    await expect(submitBtn).toBeDisabled();

    // Fill name only
    await page.fill('#name', 'Incomplete User');
    await expect(submitBtn).toBeDisabled();

    // Fill email
    await page.fill('#email', 'valid@tbz.ch');
    await expect(submitBtn).toBeEnabled();
  });
});
