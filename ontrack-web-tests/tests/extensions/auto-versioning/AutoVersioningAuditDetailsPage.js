import {expect} from "@playwright/test";
import {confirmBox} from "../../support/confirm";
import {waitUntilCondition} from "../../support/timing";

export class AutoVersioningAuditDetailsPage {
    constructor(page, ontrack, uuid) {
        this.page = page
        this.ontrack = ontrack
        this.uuid = uuid
    }

    async goTo() {
        await this.page.goto(`${this.ontrack.connection.ui}/extension/auto-versioning/audit/detail/${this.uuid}`)
        await this.expectOnPage()
    }

    async expectOnPage() {
        await expect(this.page.getByText("Auto-versioning audit entry")).toBeVisible()
        await expect(this.page.getByText(this.uuid).nth(1)).toBeVisible()
    }

    async reschedule() {
        const button = this.page.getByRole('button', {name: 'Reschedule'})
        await expect(button).toBeVisible()
        await button.click()
        await confirmBox(this.page, "Reschedule auto-versioning", {okText: "Yes"})
    }

    async getState() {
        const text = this.page.getByTestId('audit-state-0')
        return await text.innerText()
    }

    async waitState(expectedState) {
        await waitUntilCondition({
            page: this.page,
            message: 'Aborted entry is displayed',
            condition: async () => {
                const state = await this.getState()
                if (state === expectedState) {
                    return true
                } else {
                    this.page.reload()
                    return false
                }
            }
        })
    }

    async expectPRLink(prName) {
        await expect(this.page.getByRole('link', {name: prName, exact: true})).toBeVisible()
    }

}