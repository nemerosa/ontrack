import {test} from "@playwright/test";
import {manualApprovalInPipelinePage} from "./manualApprovalFixtures";

test('pipeline status refreshed when inputs completed', async ({page}) => {
    await manualApprovalInPipelinePage(page)
})

test('pipeline marked as deploying', async ({page}) => {
    const {pipelineActions} = await manualApprovalInPipelinePage(page)
    // Pipeline is now ready to be set in "deploying" mode
    await pipelineActions.checkDeployingAction()
    await pipelineActions.deploying()
    await pipelineActions.checkDeployingAction({visible: false})
    // Pipeline can now be deployed
    await pipelineActions.checkDeployedAction()
    await pipelineActions.deployed()
    await pipelineActions.checkDeployedAction({visible: false})
})
