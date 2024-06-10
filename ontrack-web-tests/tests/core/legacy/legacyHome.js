import {expect} from "@playwright/test";

export class LegacyHomePage {

    constructor(page) {
        this.page = page
    }

    async checkCreateProject() {
        await expect(this.page.getByRole('button', {name: 'Create Project'})).toBeVisible()
    }

    async checkNextUI() {
        await expect(this.page.getByRole('button', {name: 'Next UI'})).toBeVisible()
    }

}