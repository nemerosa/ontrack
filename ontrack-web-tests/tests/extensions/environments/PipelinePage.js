import {ui} from "@ontrack/connection";
import {expect} from "@playwright/test";
import {PipelineActions} from "./PipelineActions";

export class PipelinePage {
    constructor(page, pipeline) {
        this.page = page
        this.pipeline = pipeline
    }

    async goTo() {
        await this.page.goto(`${ui()}/extension/environments/pipeline/${this.pipeline.id}`)
        await expect(this.page.getByText(`Slot ${this.pipeline.slot.environment.name} - ${this.pipeline.slot.project.name}`)).toBeVisible()
        await expect(this.page.getByText(`Pipeline #${this.pipeline.number}`)).toBeVisible()
    }

    async checkPipelineActions() {
        await expect(this.page.getByTestId(`pipeline-actions-${this.pipeline.id}`)).toBeVisible()
        return new PipelineActions(this.page, this.pipeline)
    }
}