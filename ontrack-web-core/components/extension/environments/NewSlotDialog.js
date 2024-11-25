import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {Form, Input} from "antd";
import SelectProject from "@components/projects/SelectProject";
import SelectEnvironmentIds from "@components/extension/environments/SelectEnvironmentIds";
import {gql} from "graphql-request";
import {useContext} from "react";
import {EventsContext} from "@components/common/EventsContext";

export const useNewSlotDialog = () => {
    const eventsContext = useContext(EventsContext)
    return useFormDialog({
        onSuccess: () => {
            eventsContext.fireEvent("slot.created")
        },
        query: gql`
            mutation CreateSlots(
                $projectId: Int!,
                $qualifier: String,
                $description: String,
                $environmentIds: [String!]!,
            ) {
                createSlots(input: {
                    projectId: $projectId,
                    qualifier: $qualifier,
                    description: $description,
                    environmentIds: $environmentIds,
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
        userNode: 'createSlots',
    })
}

export default function NewSlotDialog({newSlotDialog}) {
    return (
        <>
            <FormDialog dialog={newSlotDialog}>
                <Form.Item
                    name="projectId"
                    label="Project"
                    rules={[
                        {
                            required: true,
                            message: 'Project is required.',
                        },
                    ]}
                >
                    <SelectProject idAsValue={true}/>
                </Form.Item>
                <Form.Item
                    name="qualifier"
                    label="Qualifier for the project"
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    name="description"
                    label="Description"
                >
                    <Input.TextArea/>
                </Form.Item>
                <Form.Item
                    name="environmentIds"
                    label="Environments"
                >
                    <SelectEnvironmentIds/>
                </Form.Item>
            </FormDialog>
        </>
    )
}