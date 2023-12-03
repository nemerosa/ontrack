import {expect} from "@playwright/test";

export class ValidationRunHistoryDialog {
    constructor(page, run) {
        this.page = page
        this.run = run
    }

    async waitFor() {
        await expect(this.page.getByText(`Runs for ${this.run.validationStamp.name} in build ${this.run.build.name}`)).toBeVisible()
    }

    async selectStatus(status) {
        const id = `validation-run-status-${this.run.id}`
        await this.page.locator(`#${id}`).click()
        await this.page.getByText(status, {exact: true}).click()
    }

    async setDescription(text) {
        return this.page.getByPlaceholder("Optional description").fill(text)
    }

    async addStatus() {
        await this.page.getByRole('button', {name: "Add"}).click()
        return expect(this.page.getByRole('button', {name: "Add"})).toBeDisabled()
    }

    async checkStatus(expectedStatus, expectedMessage) {
        await this.page.getByText(expectedStatus, {exact: true})
        await this.page.getByText(expectedMessage, {exact: true})
    }
}
