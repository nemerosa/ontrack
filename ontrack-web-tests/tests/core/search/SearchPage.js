import {expect} from "@playwright/test";

export class SearchPage {

    constructor(page, ontrack) {
        this.page = page
        this.ontrack = ontrack
    }

    async expectOnPage() {
    }

    async expectProjectResultPresent(name) {
        const link = this.page.getByRole('link', {name, exact: true})
        await expect(link).toBeVisible()
    }

    async clickProjectResult(name) {
        const link = this.page.getByRole('link', {name, exact: true})
        await link.click()
    }
}