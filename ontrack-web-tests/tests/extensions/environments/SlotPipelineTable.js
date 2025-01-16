import {expect} from "@playwright/test";
import {SlotPipelineRow} from "./SlotPipelineRow";

export class SlotPipelineTable {

    constructor(page, slot) {
        this.page = page
        this.slot = slot
    }

    locateTable() {
        return this.page.getByTestId(`slot-pipelines-${this.slot.id}`);
    }

    async expectToBeVisible() {
        const locator = this.locateTable()
        await expect(locator).toBeVisible()
    }

    async getSlotPipelineRow(pipelineId) {
        const row = new SlotPipelineRow(this.page, this.slot, pipelineId)
        await row.expectToBeVisible()
        return row
    }

}