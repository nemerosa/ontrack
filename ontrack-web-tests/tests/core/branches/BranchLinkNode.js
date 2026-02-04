import {expect} from "@playwright/test";

export class BranchLinkNode {
    constructor(page, ontrack, node) {
        this.page = page
        this.ontrack = ontrack
        this.node = node
    }

    async expectPRLink(prName) {
        await expect(this.node.getByRole('link', {name: prName, exact: true})).toBeVisible()
    }

    async expectNoPRStatus(prName) {
        const statusComponent = this.node.locator(`[data-pr-name="${prName}"].ot-pr-status`)
        await expect(statusComponent).not.toBeVisible()
    }

    async expectPRStatus(prName, status) {
        const statusComponent = this.node.locator(`[data-pr-name="${prName}"].ot-pr-status`)
        await expect(statusComponent).toHaveText(status)
    }
}