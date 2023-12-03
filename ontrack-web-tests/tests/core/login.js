import {credentials, ui} from "@ontrack/connection";
import {expect} from "@playwright/test";

export const login = async page => {
    const {username, password} = credentials()
    await page.goto(ui())
    // We expect to land on the Legacy UI login page
    await expect(page).toHaveTitle(/Ontrack - Sign in/)
    // Filling the username & password
    await page.getByPlaceholder("User name").fill(username)
    await page.getByPlaceholder("Password").fill(password)
    // Launching the login
    await page.getByText("Sign in", {exact: true}).click()
    // We expect to be on the Next UI home page now
    return expect(page.getByText("Dashboard", {exact: true})).toBeVisible()
};