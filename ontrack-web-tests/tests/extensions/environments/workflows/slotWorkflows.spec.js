import {expect, test} from "@playwright/test";
import {
    waitForPipelineToBeRunnable,
    waitForPipelineWorkflowToBeFinished,
    withSlotWorkflow
} from "./slotWorkflowsFixtures";
import {createPipeline} from "../pipelineFixtures";
import {graphQLCall} from "@ontrack/graphql";
import {ontrack} from "@ontrack/ontrack";
import {gql} from "graphql-request";
import {login} from "../../../core/login";
import {PipelinePage} from "../PipelinePage";
import {createSlot} from "../slotFixtures";
import {addSlotWorkflow} from "@ontrack/extensions/environments/workflows";
import {gqlPipelineData} from "@ontrack/extensions/environments/environments";
import {waitUntilCondition} from "../../../support/timing";

test('API - workflows on creation participate into the pipeline check list', async ({page}) => {
    const {slot, project} = await withSlotWorkflow({trigger: 'CANDIDATE'})
    const {pipeline} = await createPipeline({project, slot})

    // Pipeline was just created, workflow is running into the background
    // The pipeline must be marked as non-deployable
    let data = await graphQLCall(
        ontrack().connection,
        gql`
            query PipelineDeploymentStatus($pipelineId: String!) {
                slotPipelineById(id: $pipelineId) {
                    runAction {
                        ok
                    }
                    slot {
                        workflows {
                            slotWorkflowInstanceForPipeline(pipelineId: $pipelineId) {
                                workflowInstance {
                                    status
                                }
                            }
                        }
                    }
                }
            }
        `,
        {
            pipelineId: pipeline.id,
        }
    )
    await expect(data.slotPipelineById.runAction.ok).toBe(false)
    let slotWorkflows = data.slotPipelineById.slot.workflows
    await expect(slotWorkflows.length).toBe(1)
    let slotWorkflowInstance = slotWorkflows[0].slotWorkflowInstanceForPipeline
    await expect(slotWorkflowInstance.workflowInstance.status).toBe('RUNNING')

    // Waiting for the deployment to be runnable
    await waitForPipelineToBeRunnable(page, pipeline.id)

    // Checking that the pipeline is now deployable
    data = await graphQLCall(
        ontrack().connection,
        gql`
            query PipelineDeploymentStatus($pipelineId: String!) {
                slotPipelineById(id: $pipelineId) {
                    runAction {
                        ok
                    }
                    slot {
                        workflows {
                            slotWorkflowInstanceForPipeline(pipelineId: $pipelineId) {
                                workflowInstance {
                                    status
                                }
                            }
                        }
                    }
                }
            }
        `,
        {
            pipelineId: pipeline.id,
        }
    )
    await expect(data.slotPipelineById.runAction.ok).toBe(true)
    slotWorkflows = data.slotPipelineById.slot.workflows
    await expect(slotWorkflows.length).toBe(1)
    slotWorkflowInstance = slotWorkflows[0].slotWorkflowInstanceForPipeline
    await expect(slotWorkflowInstance.workflowInstance.status).toBe('SUCCESS')
})

test('workflows on creation participate into the pipeline check list', async ({page}) => {
    const {slot, project, slotWorkflow} = await withSlotWorkflow({trigger: 'CANDIDATE'})
    const {pipeline} = await createPipeline({project, slot})

    await waitForPipelineToBeRunnable(page, pipeline.id)

    await login(page)

    const pipelinePage = new PipelinePage(page, pipeline)
    await pipelinePage.goTo()

    await pipelinePage.expectRuleStatusProgress({value: 100})
    await pipelinePage.checkRunAction({})

    // Check workflow state in the UI
    const pipelineWorkflow = await pipelinePage.getWorkflow(slotWorkflow.id)
    await pipelineWorkflow.checkState({
        status: 'Success',
        name: 'Test',
    })
})

test('workflows on creation participate into the pipeline progress', async ({page}) => {
    const {slot, project} = await createSlot(ontrack())
    // Adding two workflows, one successful, one with error
    const slotWorkflowSuccess = await addSlotWorkflow({
        slot,
        trigger: 'CANDIDATE',
        workflowYaml: `
            name: Success
            nodes:
              - id: test
                executorId: mock
                data:
                    text: Success
                    error: false
        `
    })
    const slotWorkflowError = await addSlotWorkflow({
        slot,
        trigger: 'CANDIDATE',
        workflowYaml: `
            name: Error
            nodes:
              - id: test
                executorId: mock
                data:
                    text: Error
                    error: true
        `
    })
    const {pipeline} = await createPipeline({project, slot})

    await waitForPipelineWorkflowToBeFinished(page, pipeline.id, slotWorkflowSuccess)
    await waitForPipelineWorkflowToBeFinished(page, pipeline.id, slotWorkflowError)

    await login(page)

    const pipelinePage = new PipelinePage(page, pipeline)
    await pipelinePage.goTo()

    await pipelinePage.expectRuleStatusProgress({value: 50})
})

test('API - workflow on promotion leading to the deployment of a build', async ({page}) => {
    // Creating a slot
    const {slot, project} = await createSlot(ontrack())

    // Creating a promotion
    const branch = await project.createBranch()
    const pl = await branch.createPromotionLevel()

    // Creating a subscription on promotions to run a workflow
    await pl.subscribe({
        name: "Deployment on promotion",
        events: ['new_promotion_run'],
        channel: 'workflow',
        channelConfig: {
            workflow: {
                name: "Deployment",
                nodes: [
                    {
                        id: "start",
                        executorId: "slot-pipeline-creation",
                        data: {
                            environment: slot.environment.name,
                        }
                    },
                    {
                        id: "deploying",
                        parents: [
                            {id: "start"}
                        ],
                        executorId: "slot-pipeline-deploying",
                        data: {}
                    },
                    {
                        id: "deployed",
                        parents: [
                            {id: "deploying"}
                        ],
                        executorId: "slot-pipeline-deployed",
                        data: {}
                    },
                ]
            }
        }
    })

    // Creating a build and promoting it
    const build = await branch.createBuild()
    await build.promote(pl)

    let pipeline
    await waitUntilCondition({
        page,
        condition: async () => {
            // Getting the current pipeline of the slot and checking its status
            const data = await graphQLCall(
                ontrack().connection,
                gql`
                    query SlotPipeline($slotId: String!) {
                        slotById(id: $slotId) {
                            currentPipeline {
                                ...PipelineData
                            }
                        }
                    }
                    ${gqlPipelineData}
                `,
                {slotId: slot.id}
            )
            pipeline = data.slotById?.currentPipeline
            return pipeline && pipeline.status === 'DONE'
        },
        message: "Pipeline not created or not deployed"
    })

    await expect(pipeline.number).toStrictEqual(1)

})

test('failing workflows on deploying block the deployment completion', async ({page}) => {
    const {slot, project} = await createSlot(ontrack())
    // Adding two workflows, one successful, one with error
    const slotWorkflowSuccess = await addSlotWorkflow({
        slot,
        trigger: 'RUNNING',
        workflowYaml: `
            name: Success
            nodes:
              - id: test
                executorId: mock
                data:
                    text: Success
                    error: false
        `
    })
    const slotWorkflowError = await addSlotWorkflow({
        slot,
        trigger: 'RUNNING',
        workflowYaml: `
            name: Error
            nodes:
              - id: test
                executorId: mock
                data:
                    text: Error
                    error: true
        `
    })

    // Creating a pipeline and starting its deployment
    const {pipeline} = await createPipeline({project, slot})
    await ontrack().environments.startPipeline({pipeline})

    await waitForPipelineWorkflowToBeFinished(page, pipeline.id, slotWorkflowSuccess)
    await waitForPipelineWorkflowToBeFinished(page, pipeline.id, slotWorkflowError)

    await login(page)

    const pipelinePage = new PipelinePage(page, pipeline)
    await pipelinePage.goTo()

    await pipelinePage.checkFinishAction({disabled: true})
})

test('going to the workflow instance from a pipeline workflow', async ({page}) => {
    const {slot, project, slotWorkflow} = await withSlotWorkflow({trigger: 'CANDIDATE'})
    const {pipeline} = await createPipeline({project, slot})

    await waitForPipelineToBeRunnable(page, pipeline.id)

    await login(page)

    const pipelinePage = new PipelinePage(page, pipeline)
    await pipelinePage.goTo()

    const pipelineWorkflow = await pipelinePage.getWorkflow(slotWorkflow.id)
    const workflowInstancePage = await pipelineWorkflow.goToWorkflowInstance()

    await workflowInstancePage.checkStatus('Success')
})
