import {test} from "@playwright/test";
import {ontrack} from "@ontrack/ontrack";
import {login} from "../../core/login";
import {PipelinePage} from "./PipelinePage";
import {createSlot} from "./slotFixtures";

test('manual approval on the pipeline page', async ({page}) => {
    const {project, slot} = await createSlot(ontrack())
    await ontrack().environments.addManualApproval({slot})

    const branch = await project.createBranch()
    const build = await branch.createBuild()

    const pipeline = await slot.createPipeline({build})

    await login(page)

    const pipelinePage = new PipelinePage(page, pipeline)
    await pipelinePage.goTo()

    const pipelineActions = await pipelinePage.checkPipelineActions()
    await pipelineActions.expectManualInputButton()

    await pipelineActions.manualInput({
        actions: (dialog) => {
            dialog.getByLabel('Approval', {exact: true}).click()
            dialog.getByLabel('Approval message').fill("OK for me")
        }
    })

    await pipelineActions.expectManualInputButton(false)
})
