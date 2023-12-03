// @ts-check
const { test, expect } = require('@playwright/test');

test('login', async ({page}) => {
    await page.goto('http://localhost:3000')
    // We expect to land on the Legacy UI login page
    await expect(page).toHaveTitle(/Ontrack - Sign in/)
    // Filling the user name & password
    await page.getByPlaceholder("User name").fill("admin")
    await page.getByPlaceholder("Password").fill("admin")
    // Launching the login
    await page.getByText("Sign in", { exact: true }).click()
    // We expect to be on the Next UI home page now
    await expect(page.getByText("Dashboard", { exact: true })).toBeVisible()
})
