import {expect} from "@playwright/test";
import {AutoVersioningAuditRow} from "./AutoVersioningAuditRow";

export class AutoVersioningAuditPage {
    constructor(page, ontrack) {
        this.page = page
        this.ontrack = ontrack
    }

    async goTo() {
        await this.page.goto(`${this.ontrack.connection.ui}/extension/auto-versioning/audit/global`)
        await expect(this.page.getByText("Auto-versioning audit")).toBeVisible()
    }

    async getEntryRow(uuid) {
        const row = new AutoVersioningAuditRow(this.page, this.ontrack, uuid)
        await row.expectToBeVisible()
        return row
    }
}