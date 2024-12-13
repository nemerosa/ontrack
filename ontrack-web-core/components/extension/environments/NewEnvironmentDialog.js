import {Form, Input, InputNumber, Select} from "antd";
import {gql} from "graphql-request";
import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {EventsContext} from "@components/common/EventsContext";
import {useContext} from "react";

export function useNewEnvironmentDialog() {
    const eventsContext = useContext(EventsContext)
    return useFormDialog({
        onSuccess: (env) => {
            eventsContext.fireEvent("environment.created", {id: env.id})
        },
        prepareValues: (values) => {
            return {
                ...values,
                description: values.description ?? '',
            }
        },
        query: gql`
            mutation CreateEnvironment(
                $name: String!,
                $description: String,
                $order: Int!,
                $tags: [String!],
            ) {
                createEnvironment(input: {
                    name: $name,
                    description: $description,
                    order: $order,
                    tags: $tags,
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
        userNode: 'createEnvironment',
    })
}

export default function NewEnvironmentDialog({newEnvironmentDialog}) {
    return (
        <>
            <FormDialog dialog={newEnvironmentDialog}>
                <Form.Item name="name"
                           label="Name"
                           extra="Unique name for the environment"
                           rules={[
                               {
                                   required: true,
                                   message: 'Environment name is required.',
                               },
                           ]}
                >
                    <Input placeholder="Environment name" allowClear/>
                </Form.Item>
                <Form.Item name="description"
                           label="Description"
                           rules={[
                               {
                                   max: 500,
                                   type: 'string',
                                   message: 'Environment description must be 500 characters long at a maximum.',
                               },
                           ]}
                >
                    <Input placeholder="Environment description" allowClear/>
                </Form.Item>
                <Form.Item name="order"
                           label="Order"
                           extra="Order number used to sort the environments between each other"
                           rules={[
                               {
                                   required: true,
                                   message: 'Environment order is required.',
                               },
                           ]}
                >
                    <InputNumber min={-100} max={100}/>
                </Form.Item>
                <Form.Item name="tags"
                           label="Tags"
                           extra="Tags used to group and filter the environments"
                >
                    <Select data-testid="tags" mode="tags"/>
                </Form.Item>
            </FormDialog>
        </>
    )
}