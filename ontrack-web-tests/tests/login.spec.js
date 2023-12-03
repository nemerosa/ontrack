// @ts-check
const {test, expect} = require('@playwright/test');
const {credentials, ui} = require("@ontrack/connection");

test('login', async ({page}) => {
    const {username, password} = credentials()
    await page.goto(ui())
    // We expect to land on the Legacy UI login page
    await expect(page).toHaveTitle(/Ontrack - Sign in/)
    // Filling the user name & password
    await page.getByPlaceholder("User name").fill(username)
    await page.getByPlaceholder("Password").fill(password)
    // Launching the login
    await page.getByText("Sign in", {exact: true}).click()
    // We expect to be on the Next UI home page now
    await expect(page.getByText("Dashboard", {exact: true})).toBeVisible()
})
