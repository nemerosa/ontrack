import {getTable} from "../../support/antd-table-support";
import {expect} from "@playwright/test";
import {AutoVersioningAuditDetailsPage} from "./AutoVersioningAuditDetailsPage";

export class AutoVersioningAuditRow {
    constructor(page, ontrack, uuid) {
        this.page = page
        this.ontrack = ontrack
        this.uuid = uuid
    }

    async expectToBeVisible() {
        this.table = await getTable(this.page, 'auto-versioning-audit-table')
        this.row = await this.table.getRow(this.uuid)
    }

    async checkState(expectedState) {
        const cell = await this.row.getCell("State")
        await expect(cell).toHaveText(expectedState)
    }

    async showDetails() {
        const link = this.row.row.getByRole('link', {name: this.uuid, exact: false})
        await expect(link).toBeVisible()
        await link.click()
        const detailsPage = new AutoVersioningAuditDetailsPage(this.page, this.ontrack, this.uuid)
        await detailsPage.expectOnPage()
        return detailsPage
    }
}