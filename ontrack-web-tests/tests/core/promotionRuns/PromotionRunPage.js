import {expect} from "@playwright/test";

export class PromotionRunPage {

    constructor(page, promotionRun) {
        this.page = page
        this.promotionRun = promotionRun
    }

    async goTo() {
        await this.page.goto(`${this.promotionRun.ontrack.connection.ui}/promotionRun/${this.promotionRun.id}`)
        await expect(this.page.getByText("has been promoted")).toBeVisible()
    }

    async assertNotificationPresent(text) {
        await expect(
            this.page.locator("#promotion-run-notifications")
                .getByText(text, {exact: true})
        ).toBeVisible()
    }

}