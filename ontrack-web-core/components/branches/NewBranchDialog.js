import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {Form, Input, Switch} from "antd";
import {gql} from "graphql-request";

export const useNewBranchDialog = ({onSuccess}) => {
    return useFormDialog({
        onSuccess,
        prepareValues: (values, {project}) => {
            return {
                ...values,
                projectId: Number(project.id),
            }
        },
        query: gql`
            mutation CreateBranch(
                $projectId: Int!,
                $name: String!,
                $description: String,
                $disabled: Boolean,
            ) {
                createBranch(input: {
                    projectId: $projectId,
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
        userNode: 'createBranch'
    })
}

export default function NewBranchDialog({dialog}) {
    return (
        <>
            <FormDialog dialog={dialog}>
                <Form.Item name="name"
                           label="Branch name"
                           rules={[
                               {
                                   required: true,
                                   message: 'Branch name is required.',
                               },
                               {
                                   max: 120,
                                   type: 'string',
                                   message: 'Branch name must be 120 characters long at a maximum.',
                               },
                               {
                                   pattern: /[A-Za-z0-9._-]+/,
                                   message: 'Branch name must contain only letters, digits, dots, underscores or dashes.',
                               },
                           ]}
                >
                    <Input placeholder="Branch name" allowClear/>
                </Form.Item>
                <Form.Item name="description"
                           label="Branch description"
                           rules={[
                               {
                                   max: 500,
                                   type: 'string',
                                   message: 'Branch description must be 500 characters long at a maximum.',
                               },
                           ]}
                >
                    <Input placeholder="Branch description" allowClear/>
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
