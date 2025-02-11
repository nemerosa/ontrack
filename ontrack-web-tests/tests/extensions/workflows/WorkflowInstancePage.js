import {expect} from "@playwright/test";
import {antdDescriptionsGetCellByLabel} from "../../support/antd-descriptions";

export class WorkflowInstancePage {

    constructor(page, workflowInstanceId) {
        this.page = page
        this.workflowInstanceId = workflowInstanceId
    }

    async expectToBeVisible() {
        const idField = antdDescriptionsGetCellByLabel(this.page, 'ID')
        await expect(idField).toBeVisible()
        await expect(idField).toHaveText(this.workflowInstanceId)
    }

    async checkStatus(status) {
        const statusField = antdDescriptionsGetCellByLabel(this.page, 'Status')
            .getByTestId("workflow-instance-status")
        await expect(statusField).toBeVisible()
        await expect(statusField).toHaveText(status)
    }
}