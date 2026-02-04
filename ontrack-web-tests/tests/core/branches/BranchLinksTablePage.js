import {expect} from "@playwright/test";
import {BranchLinksTableRow} from "./BranchLinksTableRow";

export class BranchLinksTablePage {

    constructor(page, ontrack, branch) {
        this.page = page
        this.ontrack = ontrack
        this.branch = branch
    }

    async expectOnPage() {
        await expect(this.page.getByTestId("branch-links-mode-graph")).toBeVisible()
    }

    async getAutoVersioningCell() {
        const row = new BranchLinksTableRow(this.page, this.ontrack)
        await row.expectToBeVisible()
        return await row.getAutoVersioningCell()
    }

    async loadPRStatuses() {
        const button = this.page.getByRole('button', {name: 'Load PR statuses'})
        await button.click()
    }

}