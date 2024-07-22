import {expect} from "@playwright/test";

export class BuildLinksPage {
    constructor(page) {
        this.page = page
    }

    async expectOnGraphView() {
        // We expect the switch to the opposite view ("tree") to be available
        await expect(this.page.getByTestId("build-links-mode-tree")).toBeVisible()
    }

    async expectOnTreeView() {
        // We expect the switch to the opposite view ("graph") to be available
        await expect(this.page.getByTestId("build-links-mode-graph")).toBeVisible()
    }

    async expectBuildGraphNodeVisible(build) {
        await expect(this.page
            .getByTestId(`ot-build-link-node-${build.id}`)
            .getByRole("link", {name: build.name})
        ).toBeVisible()
    }

    async expectBuildTreeNodeVisible(build) {
        await expect(this.page
            .getByRole('tree')
            .getByRole("link", {name: build.name})
        ).toBeVisible()
    }

    async switchView() {
        await this.page.locator(".ot-build-links-mode-button").click()
    }
}