import {expect} from "@playwright/test";

export class ValidationRunHistoryDialog {
    constructor(page, build, validationStamp) {
        this.page = page
        this.build = build
        this.validationStamp = validationStamp
    }

    async waitFor() {
        await expect(this.page.getByText(`Runs for ${this.validationStamp.name} in build ${this.build.name}`)).toBeVisible()
    }
}
