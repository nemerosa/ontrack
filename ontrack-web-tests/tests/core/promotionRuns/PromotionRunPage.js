import {expect} from "@playwright/test";
import {confirmBox} from "../../support/confirm";
import {AutoVersioningTrail} from "../../extensions/auto-versioning/AutoVersioningTrail";
import {NotificationsTable} from "../../extensions/notifications/NotificationsTable";

export class PromotionRunPage {

    constructor(page, promotionRun) {
        this.page = page
        this.promotionRun = promotionRun
    }

    async goTo() {
        await this.page.goto(`${this.promotionRun.ontrack.connection.ui}/promotionRun/${this.promotionRun.id}`)
        await this.expectOnPage()
    }

    async expectOnPage() {
        await expect(this.page.getByText("has been promoted").nth(0)).toBeVisible()
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

    async getAVTrail() {
        const section = this.page.getByTestId('auto-versioning-trail')
        await expect(section).toBeVisible()
        return new AutoVersioningTrail(this.page, section)
    }

    async getNotificationsTable() {
        const table = this.page.getByTestId('promotion-run-notifications').getByTestId('notification-recordings-table')
        await expect(table).toBeVisible()
        return new NotificationsTable(this.page, table)
    }

}