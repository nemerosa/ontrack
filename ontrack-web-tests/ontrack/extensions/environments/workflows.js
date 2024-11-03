import {graphQLCallMutation} from "@ontrack/graphql";
import {gql} from "graphql-request";
import {ontrack} from "@ontrack/ontrack";

export const addSlotWorkflow = async ({slot, trigger, workflowYaml}) => {
    const data = await graphQLCallMutation(
        ontrack().connection,
        'addSlotWorkflow',
        gql`
            mutation AddSlotWorkflow(
                $slotId: String!,
                $trigger: SlotWorkflowTrigger!,
                $workflowYaml: String!,
            ) {
                addSlotWorkflow(input: {
                    slotId: $slotId,
                    trigger: $trigger,
                    workflowYaml: $workflowYaml,
                }) {
                    slotWorkflow {
                        id
                        workflow {
                            name
                        }
                    }
                    errors {
                        message
                    }
                }
            }
        `,
        {
            slotId: slot.id,
            trigger,
            workflowYaml,
        }
    )

    return data.addSlotWorkflow.slotWorkflow
}