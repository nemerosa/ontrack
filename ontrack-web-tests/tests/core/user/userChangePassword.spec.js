// @ts-check
const {test, expect} = require('@playwright/test');
const {ontrack} = require("@ontrack/ontrack");
const {login, logout} = require("../login");
const {UserProfilePage} = require("./userProfile");
const {generate} = require("@ontrack/utils");

test('changing user password and login again', async ({page}) => {
    // Creating a new account
    const {username, password} = await ontrack().admin().createAccount()
    // Login using this account
    await login(page, username, password)
    // Going to the user profile page
    const userProfilePage = new UserProfilePage(page)
    await userProfilePage.goTo(true)
    // Changing the password
    const newPassword = generate("new_")
    await userProfilePage.changePassword(password, newPassword)
    // Logging out
    await logout(page)
    // Using the new password
    await login(page, username, newPassword)
})
