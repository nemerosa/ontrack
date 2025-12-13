import {Form, Input, Switch} from "antd";
import {gql} from "graphql-request";
import FormDialog, {useFormDialog} from "@components/form/FormDialog";

export function useEditProjectDialog({onSuccess}) {
    return useFormDialog({
        onSuccess: onSuccess,
        init: (form, {project}) => {
            form.setFieldsValue(project)
        },
        prepareValues: (values, {project}) => {
            return {
                ...values,
                id: Number(project.id),
                description: values.description ?? '',
                disabled: values.disabled !== null ? values.disabled : false,
            }
        },
        query: gql`
            mutation EditProject(
                $id: Int!,
                $name: String,
                $description: String,
                $disabled: Boolean,
            ) {
                updateProject(input: {
                    id: $id,
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
        userNode: 'updateProject',
    })
}

export default function EditProjectDialog({dialog}) {

    return (
        <>
            <FormDialog dialog={dialog}>
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