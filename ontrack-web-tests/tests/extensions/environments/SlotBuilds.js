import {expect} from "@playwright/test";

export class SlotBuilds {

    constructor(page, slot) {
        this.page = page
        this.slot = slot
    }

    async selectAllBuilds() {
        const section = this.#section()
        const button = section.getByLabel("Show all eligible builds")
        await expect(button).toBeVisible()
        await button.click()
    }

    async checkBuildPresent(build) {
        const link = this.#buildLink(build)
        await expect(link).toBeVisible()
    }

    async checkBuildNotPresent(build) {
        const link = this.#buildLink(build)
        await expect(link).not.toBeVisible()
    }

    #section() {
        return this.page.getByTestId('slotBuilds')
    }

    #buildLink(build) {
        const section = this.#section()
        return section.getByRole('link', {name: build.name})
    }
}