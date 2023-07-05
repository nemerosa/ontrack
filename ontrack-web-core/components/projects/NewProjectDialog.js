import {Form, Input, Switch} from "antd";
import {gql} from "graphql-request";
import FormDialog, {useFormDialog} from "@components/form/FormDialog";

export function useNewProjectDialog({onSuccess}) {
    return useFormDialog({
        onSuccess: onSuccess,
        prepareValues: (values) => {
            values.description = values.description ? values.description : ''
            values.disabled = values.disabled ? values.disabled : false
        },
        query: gql`
            mutation CreateProject(
                $name: String!,
                $description: String,
                $disabled: Boolean!,
            ) {
                createProject(input: {
                    name: $name,
                    description: $description,
                    disabled: $disabled,
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
        userNode: 'createProject',
    })
}

export default function NewProjectDialog({newProjectDialog}) {

    return (
        <>
            <FormDialog dialog={newProjectDialog}>
                <Form.Item name="name"
                           label="Name"
                           rules={[
                               {
                                   required: true,
                                   message: 'Project name is required.',
                               },
                               {
                                   max: 80,
                                   type: 'string',
                                   message: 'Project name must be 80 characters long at a maximum.',
                               },
                               {
                                   pattern: /[A-Za-z0-9._-]+/,
                                   message: 'Project name must contain only letters, digits, dots, underscores or dashes.',
                               },
                           ]}
                >
                    <Input placeholder="Project name" allowClear/>
                </Form.Item>
                <Form.Item name="description"
                           label="Description"
                           rules={[
                               {
                                   max: 500,
                                   type: 'string',
                                   message: 'Project description must be 500 characters long at a maximum.',
                               },
                           ]}
                >
                    <Input placeholder="Project description" allowClear/>
                </Form.Item>
                <Form.Item name="disabled"
                           label="Disabled"
                           valuePropName="checked"
                >
                    <Switch/>
                </Form.Item>
            </FormDialog>
        </>
    )
}