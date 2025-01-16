import {expect} from "@playwright/test";
import {confirmBox} from "../../support/confirm";

export class SlotPipelineRow {

    constructor(page, slot, pipelineId) {
        this.page = page
        this.slot = slot
        this.pipelineId = pipelineId
    }

    locateTable() {
        return this.page.getByTestId(`slot-pipelines-${this.slot.id}`);
    }

    locateRow() {
        return this.locateTable().locator(`tr[data-row-key="${this.pipelineId}"]`);
    }

    async expectToBeVisible() {
        const row = this.locateRow()
        await expect(row).toBeVisible()
    }

    async checkRunAction({visible = true}) {
        const locator = this.locateRow().getByTestId(`pipeline-deploy-${this.pipelineId}`)
        await expect(locator).toBeVisible({visible})
    }

    async running() {
        const button = this.locateRow().getByTestId(`pipeline-deploy-${this.pipelineId}`)
        await button.click()
        await confirmBox(this.page, "Running deployment")
        await expect(this.locateRow().getByText("Running", {exact: true})).toBeVisible()
    }

    async checkFinishAction({visible = true}) {
        const locator = this.locateRow().getByTestId(`pipeline-finish-${this.pipelineId}`)
        await expect(locator).toBeVisible({visible})
    }

    async finish() {
        const button = this.locateRow().getByTestId(`pipeline-finish-${this.pipelineId}`)
        await button.click()
        await confirmBox(this.page, "Deployment done")
        await expect(this.locateRow().getByText("Deployed", {exact: true})).toBeVisible()
    }

}