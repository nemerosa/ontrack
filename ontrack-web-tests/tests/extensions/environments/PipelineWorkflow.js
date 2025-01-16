import {expect} from "@playwright/test";

export class PipelineWorkflow {

    constructor(page, pipeline, slotWorkflowId) {
        this.page = page
        this.pipeline = pipeline
        this.slotWorkflowId = slotWorkflowId
    }

    locatePipelineWorkflow() {
        return this.page.getByTestId(`slot-workflow-${this.slotWorkflowId}`);
    }

    async expectToBeVisible() {
        const locator = this.locatePipelineWorkflow()
        await expect(locator).toBeVisible()
    }

    async checkState({status, name}) {
        const locator = this.locatePipelineWorkflow()
        if (status) {
            await expect(locator.getByTestId(`slot-workflow-instance-status-${this.slotWorkflowId}`)).toHaveText(status)
        }
        if (name) {
            await expect(locator.getByTestId(`slot-workflow-instance-link-${this.slotWorkflowId}`)).toHaveText(name)
        }
    }

}