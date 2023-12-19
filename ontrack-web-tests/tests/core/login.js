import {credentials, ui} from "@ontrack/connection";
import {expect} from "@playwright/test";
import {selectUserMenu} from "./userMenu";
import exp from "constants";

export const login = async (page, customerUsername, customPassword, options = {}) => {
    let username
    let password
    if (customerUsername) {
        username = customerUsername
        password = customPassword
    } else {
        const creds = credentials()
        username = creds.username
        password = creds.password
    }
    await page.goto(ui())
    // We expect to land on the Legacy UI login page
    await expect(page).toHaveTitle(/Ontrack - Sign in/)
    // Filling the username & password
    await page.getByPlaceholder("User name").fill(username)
    await page.getByPlaceholder("Password").fill(password)
    // Launching the login
    await page.getByText("Sign in", {exact: true}).click()

    // If we expect a message
    if (options.message) {
        return expect(page.getByText(options.message)).toBeVisible()
    }
    // We expect to be on the Next UI home page now
    else {
        return expect(page.getByText("Dashboard", {exact: true})).toBeVisible()
    }
};

export const logout = async (page) => {
    // Selecting the logout menu
    await selectUserMenu(page, "Sign out")
    // We expect to be on the login page again
    await expect(page).toHaveTitle(/Ontrack - Sign in/)

}