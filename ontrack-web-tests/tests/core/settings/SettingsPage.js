import {expect} from "@playwright/test";

export class SettingsPage {
    constructor(page, ontrack) {
        this.page = page
        this.ontrack = ontrack
    }

    async goTo() {
        await this.page.goto(`${this.ontrack.connection.ui}/core/admin/settings`)
        await expect(this.page.getByText("General security settings")).toBeVisible()
    }

    async selectSettings(title) {
        const settingsMenu = this.page.getByText(title);
        await expect(settingsMenu).toBeVisible()
        await settingsMenu.click()
    }
}