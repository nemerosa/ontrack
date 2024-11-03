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

    async checkRuleDeployable({name, deployable = true}) {
        const text = deployable ? "Yes" : "No"
        const id = `deployable-${name}`
        const deployableText = this.page.getByTestId(id)
        await expect(deployableText).toBeVisible()
        await expect(deployableText).toContainText(text)
    }

    async checkRuleOverridden({name, overridden = true}) {
        const text = overridden ? "Yes" : "No"
        const id = `overridden-${name}`
        const overriddenText = this.page.getByTestId(id)
        await expect(overriddenText).toBeVisible()
        await expect(overriddenText).toContainText(text)
    }

    async checkRuleDetails({configId, checks}) {
        const details = this.page.getByTestId(`details-${configId}`)
        await expect(details).toBeVisible()
        if (checks) {
            await checks(details)
        }
    }

    locatorOverrideRuleButton(name) {
        return this.page.getByTestId(`override-${name}`)
    }

    async checkOverrideRuleButton({name, visible = true}) {
        await expect(this.locatorOverrideRuleButton(name)).toBeVisible({visible})
    }

    async overrideRule({name, message}) {
        await this.locatorOverrideRuleButton(name).click()
        const messageInput = this.page.getByLabel("Message", {exact: true})
        await expect(messageInput).toBeVisible()
        await messageInput.fill(message)
        await this.page.getByRole("button", {name: "OK"}).click()
    }
}