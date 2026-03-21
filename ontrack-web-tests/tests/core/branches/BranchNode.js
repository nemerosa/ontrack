import {expect} from "@playwright/test";

export class BranchNode {

    constructor(page, ontrack, node) {
        this.page = page
        this.ontrack = ontrack
        this.node = node
    }

    async expectPromotionRunWithBadge(run, {count, type}) {
        await expect(this.node.getByTestId(`promotion-run-link-${run.id}`)).toBeVisible()
        const badge = this.node.locator("css=.ant-badge-count")
        await expect(badge).toBeVisible()
        await expect(badge).toHaveText(count.toString())
        if (type) {
            if (type === "success") {
                await expect(badge).toContainClass("ant-badge-color-green")
            }
        }
    }

}