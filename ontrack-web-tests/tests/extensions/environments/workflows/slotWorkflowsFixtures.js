import {createSlot} from "../slotFixtures";
import {ontrack} from "@ontrack/ontrack";
import {addSlotWorkflow} from "@ontrack/extensions/environments/workflows";
import {graphQLCall} from "@ontrack/graphql";
import {gql} from "graphql-request";

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

export const waitForPipelineToBeDeployable = async (page, pipelineId) => {
    const startTime = Date.now()
    let conditionMet = false
    while ((Date.now() - startTime) < 5000 && !conditionMet) {
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
        if (data.slotPipelineById.deploymentStatus.status === true) {
            conditionMet = true
            break
        }
        // Wait for a short interval before retrying
        await page.waitForTimeout(500)
    }

    if (!conditionMet) {
        throw new Error('Pipeline not ready for deployment within 5 seconds');
    }
}
