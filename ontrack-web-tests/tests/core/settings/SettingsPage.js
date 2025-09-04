import {ui} from "@ontrack/connection";
import {expect} from "@playwright/test";

export class SettingsPage {
    constructor(page) {
        this.page = page
    }

    async goTo() {
        await this.page.goto(`${ui()}/core/admin/settings`)
        await expect(this.page.getByText("General security settings")).toBeVisible()
    }

    async selectSettings(title) {
        const settingsMenu = this.page.getByText(title);
        await expect(settingsMenu).toBeVisible()
        await settingsMenu.click()
    }
}