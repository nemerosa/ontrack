import {expect} from "@playwright/test";
import {PipelineRule} from "./PipelineRule";
import {confirmBox} from "../../support/confirm";
import {PipelineWorkflow} from "./PipelineWorkflow";
import {PipelineStatus} from "./PipelineStatus";

export class PipelinePage {
    constructor(page, pipeline, ontrack) {
        this.page = page
        this.pipeline = pipeline
        this.ontrack = ontrack
    }

    async goTo() {
        await this.page.goto(`${this.ontrack.connection.ui}/extension/environments/pipeline/${this.pipeline.id}`)
        await expect(this.page.getByText(`Slot ${this.pipeline.slot.environment.name} - ${this.pipeline.slot.project.name}`)).toBeVisible()
        await expect(this.page.getByText(`Deployment #${this.pipeline.number}`)).toBeVisible()
    }

    async getAdmissionRule(ruleConfigId) {
        const rule = new PipelineRule(this.page, this.pipeline, ruleConfigId);
        await rule.expectToBeVisible()
        return rule
    }

    async expectRuleStatusProgress({present = true, value = 0, overridden = false}) {
        const runStep = this.page.getByTestId(`deployment-run-${this.pipeline.id}`)
        const pipelineProgress = runStep.getByTestId(`deployment-progress-${this.pipeline.id}`)
        await expect(pipelineProgress).toBeVisible({visible: present})
        if (present && value >= 0) {
            if (value === 100) {
                await expect(pipelineProgress).toHaveAttribute('aria-valuenow', '100')
            } else {
                await expect(pipelineProgress).toContainText(`${value}%`)
            }
        }

        const hasOverriddenClass = await pipelineProgress.evaluate((element, className) => {
            return element.classList.contains(className);
        }, 'ot-extension-environment-overridden')
        if (overridden) {
            await expect(hasOverriddenClass).toBeTruthy()
        } else {
            await expect(hasOverriddenClass).toBeFalsy()
        }
    }

    /**
     * Gets a workflow step given the ID of the slot workflow
     */
    async getWorkflow(slotWorkflowId) {
        const workflow = new PipelineWorkflow(this.page, this.pipeline, slotWorkflowId);
        await workflow.expectToBeVisible()
        return workflow
    }

    async checkRunAction({visible = true, disabled = false}) {
        const locator = this.page.getByTestId(`pipeline-deploy-${this.pipeline.id}`)
        await expect(locator).toBeVisible({visible})
        if (visible) {
            if (disabled) {
                await expect(locator).toBeDisabled()
            } else {
                await expect(locator).toBeEnabled()
            }
        }
    }

    async running() {
        const button = this.page.getByTestId(`pipeline-deploy-${this.pipeline.id}`)
        await button.click()
        await confirmBox(this.page, "Running deployment")
        await expect(this.page.getByText("Running", {exact: true})).toBeVisible()
    }

    async checkFinishAction({visible = true, disabled = false}) {
        const locator = this.page.getByTestId(`pipeline-finish-${this.pipeline.id}`)
        await expect(locator).toBeVisible({visible})
        if (visible) {
            if (disabled) {
                await expect(locator).toBeDisabled()
            } else {
                await expect(locator).toBeEnabled()
            }
        }
    }

    async finish() {
        const button = this.page.getByTestId(`pipeline-finish-${this.pipeline.id}`)
        await button.click()
        await confirmBox(this.page, "Deployment done")
        await expect(this.page.getByText("Deployed", {exact: true})).toBeVisible()
    }

    async getStatus(status) {
        const statusComponent = this.page.getByTestId(`${this.pipeline.id}-status-${status}`)
        await expect(statusComponent).toBeVisible()
        return new PipelineStatus(this.page, this.pipeline, status, statusComponent)
    }

    async getDoneStatus() {
        return await this.getStatus('DONE')
    }

    async forceDone({message}) {
        const forceCommand = this.page.getByRole('button', {name: "Force deployment"})
        await expect(forceCommand).toBeVisible()
        await forceCommand.click()

        const forceMessage = this.page.getByLabel('Message')
        await expect(forceMessage).toBeVisible()
        await forceMessage.fill(message)
        const okButton = this.page.getByRole('button', {name: 'OK'});
        await okButton.click()
    }
}