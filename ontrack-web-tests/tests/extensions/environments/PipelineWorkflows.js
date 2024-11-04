import {expect} from "@playwright/test";
import {triggerMapping} from "./workflows/slotWorkflowsFixtures";

export class PipelineWorkflows {
    constructor(page, pipeline, table) {
        this.page = page
        this.pipeline = pipeline
        this.table = table
    }

    async checkWorkflow({
                            slotWorkflowId,
                            trigger,
                            status,
                        }) {
        if (trigger) {
            const triggerComponent = this.table.getByTestId(`pipeline-workflow-${slotWorkflowId}-trigger`)
            await expect(triggerComponent).toBeVisible()
            await expect(triggerComponent).toContainText(triggerMapping[trigger])
        }
        if (status) {
            const statusComponent = this.table.getByTestId(`pipeline-workflow-${slotWorkflowId}-status`)
            await expect(statusComponent).toBeVisible()
            await expect(statusComponent).toContainText(status)
        }
    }
}