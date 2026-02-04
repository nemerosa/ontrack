import {expect} from "@playwright/test";

export class BranchLinksTableAutoVersioningCell {

    constructor(page, ontrack, cell) {
        this.page = page
        this.ontrack = ontrack
        this.cell = cell
    }

    async expectPRLink(prName) {
        await expect(this.cell.getByRole('link', {name: prName, exact: true})).toBeVisible()
    }

    async expectNoPRStatus(prName) {
        const statusComponent = this.cell.locator(`[data-pr-name="${prName}"].ot-pr-status`)
        await expect(statusComponent).not.toBeVisible()
    }

    async expectPRStatus(prName, status) {
        const statusComponent = this.cell.locator(`[data-pr-name="${prName}"].ot-pr-status`)
        await expect(statusComponent).toHaveText(status)
    }

}