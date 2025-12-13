import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {Form, Input} from "antd";
import {gql} from "graphql-request";

export const useEditAccountGroupDialog = ({refresh}) => {
    return useFormDialog({
        init: (form, {accountGroup}) => {
            form.setFieldsValue(accountGroup)
        },
        prepareValues: (values, {accountGroup}) => {
            return {
                ...values,
                id: Number(accountGroup.id),
            }
        },
        query: gql`
            mutation EditAccountGroup(
                $id: Int!,
                $name: String!,
                $description: String,
            ) {
                editAccountGroup(input: {
                    id: $id,
                    name: $name,
                    description: $description,
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
        userNode: 'editAccountGroup',
        onSuccess: refresh,
    })
}

export default function EditAccountGroupDialog({dialog}) {
    return (
        <>
            <FormDialog dialog={dialog}>
                <Form.Item
                    key="name"
                    name="name"
                    label="Name"
                    rules={[
                        {required: true, message: "Name is required"},
                    ]}
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    key="description"
                    name="description"
                    label="Description"
                >
                    <Input/>
                </Form.Item>
            </FormDialog>
        </>
    )
}
