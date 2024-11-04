import {expect, test} from "@playwright/test";
import {waitForPipelineToBeDeployable, withSlotWorkflow} from "./slotWorkflowsFixtures";
import {createPipeline} from "../pipelineFixtures";
import {graphQLCall} from "@ontrack/graphql";
import {ontrack} from "@ontrack/ontrack";
import {gql} from "graphql-request";
import {login} from "../../../core/login";
import {PipelinePage} from "../PipelinePage";

test('API - workflows on creation participate into the pipeline check list', async ({page}) => {
    const {slot, project, slotWorkflow} = await withSlotWorkflow({trigger: 'CREATION'})
    const {pipeline} = await createPipeline({project, slot})

    // Pipeline was just created, workflow is running into the background
    // The pipeline must be marked as non-deployable
    let data = await graphQLCall(
        ontrack().connection,
        gql`
            query PipelineDeploymentStatus($pipelineId: String!) {
                slotPipelineById(id: $pipelineId) {
                    deploymentStatus {
                        status
                        checks {
                            check {
                                status
                                reason
                            }
                            config {
                                ruleId
                                ruleConfig
                            }
                            ruleData
                        }
                    }
                }
            }
        `,
        {
            pipelineId: pipeline.id,
        }
    )
    await expect(data.slotPipelineById.deploymentStatus.status).toBe(false)
    await expect(data.slotPipelineById.deploymentStatus.checks.length).toBe(1)
    let check = data.slotPipelineById.deploymentStatus.checks[0]
    await expect(check.check.status).toBe(false)
    await expect(check.check.reason).toMatch(/^(Workflow started|Workflow running)$/)
    await expect(check.config.ruleId).toStrictEqual('workflow')
    await expect(check.config.ruleConfig).toStrictEqual({
        slotWorkflowId: slotWorkflow.id
    })
    await expect(check.ruleData?.slotWorkflowInstanceId).toHaveLength(36)

    // Waiting for the deployment to be deployable
    await waitForPipelineToBeDeployable(page, pipeline.id)

    // Checking that the pipeline is now deployable
    data = await graphQLCall(
        ontrack().connection,
        gql`
            query PipelineDeploymentStatus($pipelineId: String!) {
                slotPipelineById(id: $pipelineId) {
                    deploymentStatus {
                        status
                        checks {
                            check {
                                status
                                reason
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
    await expect(data.slotPipelineById.deploymentStatus.status).toBe(true)
    await expect(data.slotPipelineById.deploymentStatus.checks.length).toBe(1)
    check = data.slotPipelineById.deploymentStatus.checks[0]
    await expect(check.check.status).toBe(true)
    await expect(check.check.reason).toStrictEqual('Workflow successful')
})


test('workflows on creation participate into the pipeline check list', async ({page}) => {
    const {slot, project, slotWorkflow} = await withSlotWorkflow({trigger: 'CREATION'})
    const {pipeline} = await createPipeline({project, slot})

    await waitForPipelineToBeDeployable(page, pipeline.id)

    await login(page)

    const pipelinePage = new PipelinePage(page, pipeline)
    await pipelinePage.goTo()

    const pipelineActions = await pipelinePage.checkPipelineActions()
    await pipelineActions.expectStatusProgress({value: 100})

    await pipelinePage.checkRuleDeployable({name: slotWorkflow.id})
    await pipelinePage.checkRuleDetails({
        configId: slotWorkflow.id,
        checks: async (details) => {
            await expect(details.getByText("Test")).toBeVisible()
            await expect(details.getByText("Success")).toBeVisible()
        }
    })

    const pipelineWorkflows = await pipelinePage.getPipelineWorkflows()
    await pipelineWorkflows.checkWorkflow({
        slotWorkflowId: slotWorkflow.id,
        trigger: 'CREATION',
        status: 'Success',
    })
})
