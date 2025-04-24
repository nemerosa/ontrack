import {credentials, ui} from "@ontrack/connection";
import {expect} from "@playwright/test";
import {selectUserMenu} from "./userMenu";

export const login = async (
    page,
    customerUsername = undefined,
    customPassword = undefined,
    options = {}
) => {
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
    // We expect to land on the sign-in page
    const signIn = await signInButton(page)
    await signIn.click()
    // Filling the username and password
    await page.getByRole("textbox", {exact: false, name: "Username"}).fill(username)
    await page.getByRole("textbox", {exact: false, name: "Password"}).fill(password)
    // Launching the login
    await page.getByRole("button", {name: "Sign In", exact: true}).click()

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
    await signInButton(page)
}

const signInButton = async (page) => {
    let button = page.getByRole("button", {name: "Sign in", exact: false});
    await expect(button).toBeVisible()
    return button
}
