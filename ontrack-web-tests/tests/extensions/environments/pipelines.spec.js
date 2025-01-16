import {test} from "@playwright/test";
import {manualApprovalInPipelinePage} from "./manualApprovalFixtures";
import {createSlot} from "./slotFixtures";
import {ontrack} from "@ontrack/ontrack";
import {createPipeline} from "./pipelineFixtures";
import {PipelinePage} from "./PipelinePage";
import {login} from "../../core/login";
import {SlotPage} from "./SlotPage";

test('pipeline status refreshed when inputs completed', async ({page}) => {
    await manualApprovalInPipelinePage(page)
})

test('pipeline marked as running after manual approval', async ({page}) => {
    const {pipelinePage} = await manualApprovalInPipelinePage(page)
    // Pipeline is now ready to be set in "deploying" mode
    await pipelinePage.checkRunAction({disabled: false})
    await pipelinePage.running()
    await pipelinePage.checkRunAction({visible: false})
    // Pipeline can now be deployed
    await pipelinePage.checkFinishAction({disabled: false})
    await pipelinePage.finish()
    await pipelinePage.checkFinishAction({visible: false})
})

test('pipeline lifecycle', async ({page}) => {
    const {slot, project} = await createSlot(ontrack())
    const {pipeline} = await createPipeline({project, slot})

    await login(page)
    const pipelinePage = new PipelinePage(page, pipeline)
    await pipelinePage.goTo()

    await pipelinePage.checkRunAction({})
    await pipelinePage.running()
    await pipelinePage.checkRunAction({visible: false})

    await pipelinePage.checkFinishAction({})
    await pipelinePage.finish()
    await pipelinePage.checkFinishAction({visible: false})
})

test('pipeline lifecycle from the slot page', async ({page}) => {
    const {slot, project} = await createSlot(ontrack())
    const {pipeline} = await createPipeline({project, slot})

    await login(page)
    const slotPage = new SlotPage(page, slot)
    await slotPage.goTo()

    const slotPipelineTable = await slotPage.getSlotPipelineTable()
    const slotPipelineRow = await slotPipelineTable.getSlotPipelineRow(pipeline.id)

    await slotPipelineRow.checkRunAction({})
    await slotPipelineRow.running()
    await slotPipelineRow.checkRunAction({visible: false})

    await slotPipelineRow.checkFinishAction({})
    await slotPipelineRow.finish()
    await slotPipelineRow.checkFinishAction({visible: false})

})
