import {test} from "@playwright/test";
import {ontrack} from "@ontrack/ontrack";
import {createSlot} from "./Slots";
import {login} from "../../core/login";
import {EnvironmentsPage} from "./Environments";
import {PipelinePage} from "./PipelinePage";

test('manual approval on the environments page', async ({page}) => {
    const {project, slot} = await createSlot(ontrack())
    await ontrack().environments.addManualApproval({slot})

    const branch = await project.createBranch()
    const build = await branch.createBuild()

    const pipeline = await slot.createPipeline({build})

    await login(page)

    const environmentsPage = new EnvironmentsPage(page)
    await environmentsPage.goTo()

    const {pipelineActions} = await environmentsPage.checkPipelineCard(pipeline)
    await pipelineActions.expectManualInputButton()

    await pipelineActions.manualInput({
        actions: (dialog) => {
            dialog.getByLabel('Approval', { exact: true }).click()
            dialog.getByLabel('Approval message').fill("OK for me")
        }
    })

    await pipelineActions.expectManualInputButton(false)
})

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
            dialog.getByLabel('Approval', { exact: true }).click()
            dialog.getByLabel('Approval message').fill("OK for me")
        }
    })

    await pipelineActions.expectManualInputButton(false)
})
