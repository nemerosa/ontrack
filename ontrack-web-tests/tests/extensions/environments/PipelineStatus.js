import {expect} from "@playwright/test";

export class PipelineStatus {

    constructor(page, pipeline, status, statusComponent) {
        this.page = page
        this.pipeline = pipeline
        this.status = status
        this.statusComponent = statusComponent
    }

    async expectForcingMessage(message) {
        const messageComponent = this.statusComponent.getByTestId(`${this.pipeline.id}-status-message-${this.status}`)
        await expect(messageComponent).toBeVisible()
        await expect(messageComponent).toHaveText(message)
    }
}