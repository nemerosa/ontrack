const {test, expect} = require('@playwright/test')
const {login} = require("../../core/login");
const {ontrack} = require("@ontrack/ontrack");
const {SettingsPage} = require("../../core/settings/SettingsPage");

test('saving existing GitHub Ingestion settings', async ({page}) => {
    // Creating the GH Ingestion settings
    await ontrack().settings.gitHubIngestion.saveSettings({
        token: "some-secret",
        retentionDays: 14,
        orgProjectPrefix: false,
        indexationInterval: 30,
        repositoryIncludes: '.*',
        repositoryExcludes: '',
        issueServiceIdentifier: '',
        enabled: true,
    })
    // Login
    await login(page)
    // Going to the settings page
    const settingsPage = new SettingsPage(page)
    await settingsPage.goTo()
    // Selecting the GitHub Ingestion settings
    await settingsPage.selectSettings("GitHub ingestion")
    // Changing the settings but leaving the Token blank
    await page.getByLabel("Exclude repositories").fill("some-repository")
    const submit = page.getByRole("button", {name: "Submit"});
    await submit.click()
    // Settings have been saved (but there is no way on the client side to check if the token has been saved or not)
    await expect(submit).toBeEnabled()
    await expect(page.getByText("problem", {exact: false})).not.toBeVisible()
})
