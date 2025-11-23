import {expect} from "@playwright/test";
import {confirmBox} from "../../support/confirm";

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

    async deletePromotionRun() {
        const button = this.page.getByRole('button', {name: 'Delete'})
        await expect(button).toBeVisible()
        await button.click()
        await confirmBox(this.page, "Removing this promotion run", {okText: "Confirm deletion"})
    }

}