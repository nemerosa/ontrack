import {expect} from "@playwright/test";

export class PipelineActions {
    constructor(page, pipeline) {
        this.page = page
        this.pipeline = pipeline
    }

    async expectManualInputButton(present = true) {
        const pipelineActions = this.page.getByTestId(`pipeline-actions-${this.pipeline.id}`)
        const inputNeededButton = pipelineActions.getByTestId('pipeline-input-needed');
        if (present) {
            await expect(inputNeededButton).toBeVisible()
        } else {
            await expect(inputNeededButton).not.toBeVisible()
        }
    }

    async manualInput({actions}) {
        const pipelineActions = this.page.getByTestId(`pipeline-actions-${this.pipeline.id}`)
        pipelineActions.getByTestId('pipeline-input-needed').click()

        const okButton = this.page.getByRole('button', {name: 'OK'});
        await expect(okButton).toBeVisible()

        if (actions) {
            actions(this.page)
        }

        okButton.click()
    }
}