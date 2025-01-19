import {expect} from "@playwright/test";
import {WorkflowInstancePage} from "../workflows/WorkflowInstancePage";

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

    async checkWorkflowOverridden({overridden = true}) {
        const locator = this.locatePipelineWorkflow()
        const overriddenLocator = locator.getByTestId(`overridden-${this.slotWorkflowId}`)
        if (overridden) {
            await expect(overriddenLocator).toBeVisible()
        } else {
            await expect(overriddenLocator).not.toBeVisible()
        }
    }

    locatorOverrideWorkflowButton() {
        const locator = this.locatePipelineWorkflow()
        return locator.getByTestId(`override-${this.slotWorkflowId}`)
    }

    async checkOverrideWorkflowButton({visible = true}) {
        await expect(this.locatorOverrideWorkflowButton()).toBeVisible({visible})
    }

    async overrideWorkflow({message}) {
        await this.locatorOverrideWorkflowButton().click()
        const messageInput = this.page.getByLabel("Message", {exact: true})
        await expect(messageInput).toBeVisible()
        await messageInput.fill(message)
        await this.page.getByRole("button", {name: "OK"}).click()
    }

    async goToWorkflowInstance() {
        const locator = this.locatePipelineWorkflow()
        const link = locator.getByTestId(`slot-workflow-instance-link-${this.slotWorkflowId}`)
        const workflowInstanceId = await link.getAttribute('data-workflow-instance-id')
        await expect(workflowInstanceId).toBeTruthy()
        await expect(link).toBeVisible()
        await link.click()
        const workflowInstancePage = new WorkflowInstancePage(this.page, workflowInstanceId)
        await workflowInstancePage.expectToBeVisible()
        return workflowInstancePage
    }

}