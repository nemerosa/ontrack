import {expect} from "@playwright/test";
import {AbstractImagePage} from "../common/AbstractImagePage";

export class ValidationStampPage extends AbstractImagePage {

    constructor(page, validationStamp) {
        super(page)
        this.validationStamp = validationStamp
    }


    id() {
        return `validation-stamp-image-${this.validationStamp.id}`
    }

    async goTo() {
        await this.page.goto(`${this.validationStamp.ontrack.connection.ui}/validationStamp/${this.validationStamp.id}`)
        await expect(this.page.getByText(this.validationStamp.name)).toBeVisible()
    }

}