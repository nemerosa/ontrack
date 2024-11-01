import {test} from "@playwright/test";
import {manualApprovalInEnvironmentsPage} from "./manualInputs.spec";

test('pipeline status refreshed when inputs completed', async ({page}) => {
    await manualApprovalInEnvironmentsPage(page)
})

test('pipeline marked as deploying', async ({page}) => {
    const {pipelineActions} = await manualApprovalInEnvironmentsPage(page)
    // Pipeline is now ready to be set in "deploying" mode
    await pipelineActions.checkDeployingAction()
    await pipelineActions.deploying()
    await pipelineActions.checkDeployingAction({visible: false})
    // Pipeline can now be deployed
    await pipelineActions.checkDeployedAction()
    await pipelineActions.deployed()
    await pipelineActions.checkDeployedAction({visible: false})
})
