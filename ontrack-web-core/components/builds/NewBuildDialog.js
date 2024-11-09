import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {gql} from "graphql-request";
import {Form, Input} from "antd";

export const useNewBuildDialog = ({onSuccess}) => {
    return useFormDialog({
        onSuccess,
        prepareValues: (values, {branch}) => {
            return {
                ...values,
                branchId: branch.id,
            }
        },
        query: gql`
            mutation CreateBuild(
                $branchId: Int!,
                $name: String!,
                $description: String,
            ) {
                createBuild(input: {
                    branchId: $branchId,
                    name: $name,
                    description: $description,
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
        userNode: 'createBuild',
    })
}

export default function NewBuildDialog({dialog}) {
    return (
        <>
            <FormDialog dialog={dialog}>
                <Form.Item name="name"
                           label="Name"
                           rules={[
                               {
                                   required: true,
                                   message: 'Build name is required.',
                               },
                               {
                                   max: 150,
                                   type: 'string',
                                   message: 'Build name must be 150 characters long at a maximum.',
                               },
                               {
                                   pattern: /[A-Za-z0-9._-]+/,
                                   message: 'Build name must contain only letters, digits, dots, underscores or dashes.',
                               },
                           ]}
                >
                    <Input placeholder="Build name" allowClear/>
                </Form.Item>
                <Form.Item name="description"
                           label="Description"
                           rules={[
                               {
                                   max: 500,
                                   type: 'string',
                                   message: 'Build description must be 500 characters long at a maximum.',
                               },
                           ]}
                >
                    <Input placeholder="Build description" allowClear/>
                </Form.Item>
            </FormDialog>
        </>
    )
}