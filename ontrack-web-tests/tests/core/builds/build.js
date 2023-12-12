const {ui} = require("@ontrack/connection");
const {expect} = require("@playwright/test");

export class BuildPage {

    constructor(page, build) {
        this.page = page
        this.build = build
    }

    async goTo() {
        await this.page.goto(`${ui()}/build/${this.build.id}`)
        await expect(this.page.getByText(this.build.name)).toBeVisible()
        // Widgets must be visible
        await expect(this.page.getByText("Promotions")).toBeVisible()
        await expect(this.page.getByText("Validations")).toBeVisible()
        await expect(this.page.getByText("Links")).toBeVisible()
    }

}