import {expect} from "@playwright/test";

export class PromotionRunDialog {

    constructor(page) {
        this.page = page
        this.dialog = this.page.getByTestId("promotion-run-create-dialog")
    }

    async createPromotionRun() {
        await expect(this.dialog).toBeVisible()

        // Not needed right now, but fields can be filled in using parameters

        await this.dialog.getByRole('button', {name: "OK"}).click()
    }

}