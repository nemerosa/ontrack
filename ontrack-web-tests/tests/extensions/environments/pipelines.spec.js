import {expect, test} from "@playwright/test";
import {manualApprovalInPipelinePage} from "./manualApprovalFixtures";
import {createSlot} from "./slotFixtures";
import {ontrack} from "@ontrack/ontrack";
import {createPipeline} from "./pipelineFixtures";
import {PipelinePage} from "./PipelinePage";
import {login} from "../../core/login";
import {SlotPage} from "./SlotPage";
import {addSlotWorkflow} from "@ontrack/extensions/environments/workflows";

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

test('starting a pipeline in forced DONE', async ({page}) => {
    const {
        slot,
        project,
        promotionRuleId,
        candidateWorkflow,
        doneWorkflow,
        runningWorkflow
    } = await preparePipelineForForcedDeployment()

    // Creating a pipeline in DONE state
    const {pipeline} = await createPipeline({
        project,
        slot,
        forceDone: true,
        forceDoneMessage: "Created pipeline as done",
        branchSetup: async (branch) => {
            await branch.createPromotionLevel("BRONZE")
        }
    })

    await expect(pipeline).not.toBeNull()

    // Going to the pipeline page
    await login(page)
    const pipelinePage = new PipelinePage(page, pipeline)
    await pipelinePage.goTo()

    // Checks the pipeline is marked as DONE
    await checkForcedPipeline({
        pipelinePage,
        promotionRuleId,
        candidateWorkflow,
        runningWorkflow,
        doneWorkflow,
        expectedMessage: "Created pipeline as done",
    })
})

test('forcing a pipeline in forced DONE', async ({page}) => {
    const {
        slot,
        project,
        promotionRuleId,
        doneWorkflow,
        runningWorkflow
    } = await preparePipelineForForcedDeployment()

    // Creating a pipeline in normal state
    const {pipeline} = await createPipeline({
        project,
        slot,
        branchSetup: async (branch) => {
            await branch.createPromotionLevel("BRONZE")
        }
    })

    await expect(pipeline).not.toBeNull()

    // Going to the pipeline page
    await login(page)
    const pipelinePage = new PipelinePage(page, pipeline)
    await pipelinePage.goTo()

    // Forcing the pipeline as DONE
    await pipelinePage.forceDone({
        message: "Deployment was done by other means"
    })

    // Checks the pipeline is marked as DONE
    await checkForcedPipeline({
        pipelinePage,
        promotionRuleId,
        candidateWorkflow: null, // The candidate workflow may have been run or not depending on the timing
        runningWorkflow,
        doneWorkflow,
        expectedMessage: "Deployment was done by other means",
    })
})

const preparePipelineForForcedDeployment = async () => {
    const {slot, project} = await createSlot(ontrack())

    const promotionRuleId = await ontrack().environments.addPromotionRule({slot, promotion: "BRONZE"})

    const candidateWorkflow = await addSlotWorkflow({
        slot,
        trigger: 'CANDIDATE',
        workflowYaml: `
             name: On candidate
             nodes:
               - id: start
                 executorId: mock
                 data:
                   text: Candidate
        `
    })

    const runningWorkflow = await addSlotWorkflow({
        slot,
        trigger: 'RUNNING',
        workflowYaml: `
             name: On running
             nodes:
               - id: start
                 executorId: mock
                 data:
                   text: Running
        `
    })

    const doneWorkflow = await addSlotWorkflow({
        slot,
        trigger: 'DONE',
        workflowYaml: `
             name: On done
             nodes:
               - id: start
                 executorId: mock
                 data:
                   text: Done
        `
    })

    return {
        slot,
        project,
        promotionRuleId,
        candidateWorkflow,
        runningWorkflow,
        doneWorkflow,
    }
}

const checkForcedPipeline = async ({
                                       pipelinePage,
                                       promotionRuleId,
                                       candidateWorkflow,
                                       runningWorkflow,
                                       doneWorkflow,
                                       expectedMessage,
                                   }) => {

    // Pipeline is done
    await pipelinePage.checkRunAction({visible: false})
    await pipelinePage.checkFinishAction({visible: false})

    // Checks the admission rule

    const promotionRule = await pipelinePage.getAdmissionRule(promotionRuleId)
    await promotionRule.expectToBeVisible()
    await promotionRule.expectToBeUnchecked()

    // Checks the workflows

    if (candidateWorkflow) {
        const candidateWorkflowInstance = await pipelinePage.getWorkflow(candidateWorkflow.id)
        await candidateWorkflowInstance.checkState({
            status: "Not started",
            name: "On candidate"
        })
    }

    const runningWorkflowInstance = await pipelinePage.getWorkflow(runningWorkflow.id)
    await runningWorkflowInstance.checkState({
        status: "Not started",
        name: "On running"
    })

    const doneWorkflowInstance = await pipelinePage.getWorkflow(doneWorkflow.id)
    await doneWorkflowInstance.checkState({
        status: "Success",
        name: "On done"
    })

    // Checks the forcing message

    const doneStatus = await pipelinePage.getDoneStatus()
    await doneStatus.expectForcingMessage(expectedMessage)

}
