import { expect, test } from '@playwright/test';

test('startseite wird geladen', async ({ page }) => {
  await page.goto('/');
  await expect(page).toHaveTitle(/Frontend/);
});
