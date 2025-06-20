import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {Form} from "antd";
import SelectSlotWorkflowTrigger from "@components/extension/environments/SelectSlotWorkflowTrigger";
import EditWorkflowButton from "@components/extension/workflows/EditWorkflowButton";
import {gql} from "graphql-request";

export const useSlotWorkflowDialog = ({onSuccess}) => {
    return useFormDialog({
        onSuccess,
        init: (form, {slotWorkflow}) => {
            form.setFieldValue("trigger", slotWorkflow?.trigger)
            form.setFieldValue("workflow", slotWorkflow?.workflow)
        },
        prepareValues: (values, {slot, slotWorkflow}) => {
            return {
                ...values,
                slotId: slot.id,
                slotWorkflowId: slotWorkflow?.id,
            }
        },
        query: gql`
            mutation SaveSlotWorkflow(
                $slotWorkflowId: String,
                $slotId: String!,
                $trigger: SlotPipelineStatus!,
                $workflow: JSON!,
            ) {
                saveSlotWorkflow(input: {
                    id: $slotWorkflowId,
                    slotId: $slotId,
                    trigger: $trigger,
                    workflow: $workflow,
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
        userNode: 'saveSlotWorkflow',
    })
}

export default function SlotWorkflowDialog({dialog}) {
    return (
        <>
            <FormDialog dialog={dialog}>
                <Form.Item
                    name="trigger"
                    label="Trigger"
                    extra="When will this workflow be triggered?"
                    rules={[
                        {
                            required: true,
                            message: 'Trigger is required.',
                        },
                    ]}
                >
                    <SelectSlotWorkflowTrigger/>
                </Form.Item>
                <Form.Item
                    name="workflow"
                    label="Workflow"
                    rules={[
                        {
                            required: true,
                            message: 'Workflow is required.',
                        },
                    ]}
                >
                    <EditWorkflowButton/>
                </Form.Item>
            </FormDialog>
        </>
    )
}