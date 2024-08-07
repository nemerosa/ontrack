import {ui} from "@ontrack/connection";
import {expect} from "@playwright/test";

export class ValidationStampPage {

    constructor(page, validationStamp) {
        this.page = page
        this.validationStamp = validationStamp
    }

    async goTo() {
        await this.page.goto(`${ui()}/validationStamp/${this.validationStamp.id}`)
        await expect(this.page.getByText(this.validationStamp.name)).toBeVisible()
    }

}