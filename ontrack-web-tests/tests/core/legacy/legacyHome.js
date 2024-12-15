import {expect} from "@playwright/test";

export class LegacyHomePage {

    constructor(page) {
        this.page = page
    }

    async checkCreateProject() {
        await expect(this.page.getByRole('button', {name: 'Create Project'})).toBeVisible({timeout: 15_000})
    }

    async checkNextUI() {
        await expect(this.page.getByRole('button', {name: 'Next UI'})).toBeVisible({timeout: 15_000})
    }

}