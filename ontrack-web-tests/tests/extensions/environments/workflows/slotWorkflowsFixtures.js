import {createSlot} from "../slotFixtures";
import {ontrack} from "@ontrack/ontrack";
import {addSlotWorkflow} from "@ontrack/extensions/environments/workflows";
import {graphQLCall} from "@ontrack/graphql";
import {gql} from "graphql-request";
import {waitUntilCondition} from "../../../support/timing";

export const triggerMapping = {
    CREATION: "Creation",
    DEPLOYING: "Deploying",
    DEPLOYED: "Deployed",
}

export const withSlotWorkflow = async ({trigger}) => {
    const {slot, project} = await createSlot(ontrack())
    const slotWorkflow = await addSlotWorkflow({
        slot,
        trigger,
        workflowYaml: `
            name: Test
            nodes:
              - id: start
                executorId: mock
                data:
                    text: Start
                    waitMs: 2000
              - id: end
                parents:
                  - id: start
                executorId: mock
                data:
                    text: End
        `
    })
    return {slot, project, slotWorkflow}
}

export const waitForPipelineWorkflowToBeFinished = async (page, pipelineId, slotWorkflow) => {
    await waitUntilCondition({
        page,
        condition: async () => {
            const data = await graphQLCall(
                ontrack().connection,
                gql`
                    query PipelineDeploymentStatus($pipelineId: String!) {
                        slotPipelineById(id: $pipelineId) {
                            slotWorkflowInstances {
                                slotWorkflow {
                                    id
                                }
                                workflowInstance {
                                    finished
                                }
                            }
                        }
                    }
                `,
                {
                    pipelineId: pipelineId,
                }
            )
            // Looking for the instance for the slot workflow
            const slotWorkflowInstance = data.slotPipelineById?.slotWorkflowInstances?.find(it =>
                it.slotWorkflow.id === slotWorkflow.id
            )
            // Condition is that the workflow instance is finished to run
            return slotWorkflowInstance?.workflowInstance?.finished
        },
        message: `Pipeline workflow for ${slotWorkflow.workflow.name} not finished in 5 seconds`
    })
}

export const waitForPipelineToBeDeployable = async (page, pipelineId) => {
    await waitUntilCondition({
        page,
        condition: async () => {
            const data = await graphQLCall(
                ontrack().connection,
                gql`
                    query PipelineDeploymentStatus($pipelineId: String!) {
                        slotPipelineById(id: $pipelineId) {
                            deploymentStatus {
                                status
                            }
                        }
                    }
                `,
                {
                    pipelineId: pipelineId,
                }
            )
            return (data.slotPipelineById.deploymentStatus.status === true)
        },
        message: 'Pipeline not ready for deployment within 5 seconds'
    })
}
