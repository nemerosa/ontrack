import {expect} from "@playwright/test";
import {PipelineInputDialog} from "./PipelineInputDialog";

export class PipelineRule {

    constructor(page, pipeline, ruleConfigId) {
        this.page = page
        this.pipeline = pipeline
        this.ruleConfigId = ruleConfigId
    }

    locatePipelineRule() {
        return this.page.getByTestId(`pipeline-rule-${this.ruleConfigId}`);
    }

    async expectToBeVisible() {
        const locator = this.locatePipelineRule()
        await expect(locator).toBeVisible()
    }

    async expectManualInputButton(present = true) {
        const locator = this.locatePipelineRule()
        const inputNeededButton = locator.getByTestId(`pipeline-rule-input-${this.ruleConfigId}`)
        if (present) {
            await expect(inputNeededButton).toBeVisible()
        } else {
            await expect(inputNeededButton).not.toBeVisible()
        }
    }

    async manualInput({actions}) {
        const locator = this.locatePipelineRule()
        await locator.getByTestId(`pipeline-rule-input-${this.ruleConfigId}`).click()
        const pipelineInputDialog = new PipelineInputDialog(this.page)
        await pipelineInputDialog.manualInput({actions})
    }

    async checkRuleOverridden({overridden = true}) {
        const locator = this.locatePipelineRule()
        const overriddenLocator = locator.getByTestId(`overridden-${this.ruleConfigId}`)
        if (overridden) {
            await expect(overriddenLocator).toBeVisible()
        } else {
            await expect(overriddenLocator).not.toBeVisible()
        }
    }

    locatorOverrideRuleButton() {
        const locator = this.locatePipelineRule()
        return locator.getByTestId(`override-${this.ruleConfigId}`)
    }

    async checkOverrideRuleButton({visible = true}) {
        await expect(this.locatorOverrideRuleButton()).toBeVisible({visible})
    }

    async overrideRule({message}) {
        await this.locatorOverrideRuleButton().click()
        const messageInput = this.page.getByLabel("Message", {exact: true})
        await expect(messageInput).toBeVisible()
        await messageInput.fill(message)
        await this.page.getByRole("button", {name: "OK"}).click()
    }
}