/**
 * Tests about the legacy UI.
 */

const {test} = require("@playwright/test");
const {login} = require("./login");
const {HomePage} = require("./home/home");

test.fixme('legacy home', async ({page}) => {
    // Login to the Next UI first
    await login(page)
    // Going to the legacy UI
    const homePage = new HomePage(page)
    const legacyHomePage = await homePage.legacyHome()
    // Checking the legacy home page is correctly loaded
    await legacyHomePage.checkCreateProject()
    await legacyHomePage.checkNextUI()
})