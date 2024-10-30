import {expect} from "@playwright/test";

export class PipelineCard {
    constructor(page, pipeline) {
        this.page = page
        this.pipeline = pipeline
    }

    async expectManualInputButton(present = true) {
        const pipelineCard = this.page.getByTestId(this.pipeline.id)
        const inputNeededButton = pipelineCard.getByTestId('pipeline-input-needed');
        if (present) {
            await expect(inputNeededButton).toBeVisible()
        } else {
            await expect(inputNeededButton).not.toBeVisible()
        }
    }

    async manualInput({actions}) {
        const pipelineCard = this.page.getByTestId(this.pipeline.id)
        pipelineCard.getByTestId('pipeline-input-needed').click()

        const okButton = this.page.getByRole('button', {name: 'OK'});
        await expect(okButton).toBeVisible()

        if (actions) {
            actions(this.page)
        }

        okButton.click()
    }
}