import {expect} from "@playwright/test";
import {confirmBox} from "../../support/confirm";

export class PipelineActions {
    constructor(page, pipeline) {
        this.page = page
        this.pipeline = pipeline
    }

    locatePipelineActions() {
        return this.page.getByTestId(`pipeline-actions-${this.pipeline.id}`);
    }

    async expectManualInputButton(present = true) {
        const pipelineActions = this.locatePipelineActions()
        const inputNeededButton = pipelineActions.getByTestId('pipeline-input-needed');
        if (present) {
            await expect(inputNeededButton).toBeVisible()
        } else {
            await expect(inputNeededButton).not.toBeVisible()
        }
    }

    async expectStatusProgress({present = true, value = 0, overridden = false}) {
        const pipelineActions = this.locatePipelineActions()
        const pipelineProgress = pipelineActions.getByTestId(`pipeline-progress-${this.pipeline.id}`)
        await expect(pipelineProgress).toBeVisible({visible: present})
        if (present && value >= 0) {
            if (value === 100) {
                await expect(pipelineProgress).toHaveAttribute('aria-valuenow', '100')
            } else {
                await expect(pipelineProgress).toContainText(`${value}%`)
            }
        }
    }

    async manualInput({actions}) {
        const pipelineActions = this.locatePipelineActions()
        await pipelineActions.getByTestId('pipeline-input-needed').click()

        const okButton = this.page.getByRole('button', {name: 'OK'});
        await expect(okButton).toBeVisible()

        if (actions) {
            await actions(this.page)
        }

        okButton.click()
    }

    async checkDeployingAction(config = {visible: true}) {
        const pipelineActions = this.locatePipelineActions()
        const button = pipelineActions.getByTestId(`pipeline-deploy-${this.pipeline.id}`)
        await expect(button).toBeVisible({visible: config.visible})
    }

    async deploying() {
        const pipelineActions = this.locatePipelineActions()
        const button = pipelineActions.getByTestId(`pipeline-deploy-${this.pipeline.id}`)
        await button.click()
        await confirmBox(this.page, "Deploying pipeline")
        await expect(pipelineActions.getByText("Deploying")).toBeVisible()
    }

    async checkDeployedAction(config = {visible: true}) {
        const pipelineActions = this.locatePipelineActions()
        const button = pipelineActions.getByTestId(`pipeline-finish-${this.pipeline.id}`)
        await expect(button).toBeVisible({visible: config.visible})
    }

    async deployed() {
        const pipelineActions = this.locatePipelineActions()
        const button = pipelineActions.getByTestId(`pipeline-finish-${this.pipeline.id}`)
        await button.click()
        await confirmBox(this.page, "Deployed pipeline")
        await expect(pipelineActions.getByText("Deployed")).toBeVisible()
    }
}