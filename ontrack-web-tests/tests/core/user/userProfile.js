const {ui} = require("@ontrack/connection");
const {expect} = require("@playwright/test");

export class UserProfilePage {

    constructor(page) {
        this.page = page
    }


    async goTo(expectChangePassword) {
        await this.page.goto(`${ui()}/core/admin/userProfile`)
        await expect(this.page.getByText("API tokens")).toBeVisible()
        if (expectChangePassword) {
            await expect(this.page.getByText("Change password")).toBeVisible()
        }
    }

    async changePassword(oldPassword, newPassword) {
        await this.page.getByLabel("Old password").fill(oldPassword)
        await this.page.getByLabel("New password").fill(newPassword)
        await this.page.getByLabel("Confirm password").fill(newPassword)
        await this.page.getByText("Submit", {exact: true}).click()
    }

    async generateToken(tokenName) {
        await this.page.getByPlaceholder("Token name").fill(tokenName)
        await this.page.getByText("Generate token", {exact: true}).click()

        // Reading from the clipboard is not supported in Firefox
        // await this.page.getByTitle("Copies the generated token into the clipboard.").click()
        // const text = await this.page.evaluate(() => navigator.clipboard.readText())

        return this.page.getByTestId("generatedToken").inputValue()
    }

}
