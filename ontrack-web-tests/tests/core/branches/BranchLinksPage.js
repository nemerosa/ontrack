import {expect} from "@playwright/test";
import {BranchLinkNode} from "./BranchLinkNode";
import {BranchLinksTablePage} from "./BranchLinksTablePage";

export class BranchLinksPage {

    constructor(page, ontrack, branch) {
        this.page = page
        this.ontrack = ontrack
        this.branch = branch
    }

    async expectOnPage() {
        await expect(this.page.getByTestId("branch-links-mode-table")).toBeVisible()
    }

    async loadPRStatuses() {
        const button = this.page.getByRole('button', {name: 'Load PR statuses'})
        await button.click()
    }

    async getLinkNode(targetBranchProjectName, sourceBranchProjectName) {
        const node = this.page.getByTestId(`ot-branch-link-node-${targetBranchProjectName}-${sourceBranchProjectName}`)
        await expect(node).toBeVisible()
        return new BranchLinkNode(this.page, this.ontrack, node)
    }

    async displayAsTable() {
        await this.page.getByTestId("branch-links-mode-table").click()
        const branchLinksTablePage = new BranchLinksTablePage(this.page, this.ontrack, this.branch)
        await branchLinksTablePage.expectOnPage()
        return branchLinksTablePage
    }

}