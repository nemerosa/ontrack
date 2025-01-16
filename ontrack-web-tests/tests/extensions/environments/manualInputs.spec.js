import {test} from "@playwright/test";
import {prepareManualApprovalInPipelinePage} from "./manualApprovalFixtures";

test('manual approval on the pipeline page', async ({page}) => {
    const {pipelinePage, ruleConfigId} = await prepareManualApprovalInPipelinePage(page)

    const admissionRule = await pipelinePage.getAdmissionRule(ruleConfigId)
    await admissionRule.expectManualInputButton()

    await admissionRule.manualInput({
        actions: async (dialog) => {
            await dialog.getByLabel('Approval', {exact: true}).click()
            await dialog.getByLabel('Approval message').fill("OK for me")
        }
    })

    await admissionRule.expectManualInputButton(false)
})
