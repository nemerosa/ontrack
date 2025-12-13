import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {Form, Input} from "antd";
import {gql} from "graphql-request";

export const useCreateAccountGroupDialog = ({refresh}) => {
    return useFormDialog({
        query: gql`
            mutation CreateAccountGroup(
                $name: String!,
                $description: String,
            ) {
                createAccountGroup(input: {
                    name: $name,
                    description: $description,
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
        userNode: 'createAccountGroup',
        onSuccess: refresh,
    })
}

export default function CreateAccountGroupDialog({dialog}) {
    return (
        <>
            <FormDialog dialog={dialog}>
                <Form.Item
                    label="Name"
                    name="name"
                    rules={[
                        {
                            required: true,
                            message: "Name is required",
                        },
                    ]}
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    label="Description"
                    name="description"
                >
                    <Input/>
                </Form.Item>
            </FormDialog>
        </>
    )
}