import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {Form, Input} from "antd";
import SelectMultipleAccountGroups from "@components/core/admin/account-management/SelectMultipleAccountGroups";
import {gql} from "graphql-request";

export const useEditAccountDialog = ({refresh}) => {
    return useFormDialog({
        init: (form, {account}) => {
            form.setFieldsValue({
                ...account,
                groups: account.groups.map(g => g.id),
            })
        },
        prepareValues: (values, {account}) => {
            return {
                ...values,
                id: Number(account.id),
                groups: values.groups.map(gid => Number(gid)),
            }
        },
        query: gql`
            mutation EditAccount(
                $id: Int!,
                $fullName: String!,
                $groups: [Int!]!,
            ) {
                editAccount(input: {
                    id: $id,
                    fullName: $fullName,
                    groups: $groups,
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
        userNode: 'editAccount',
        onSuccess: refresh,
    })
}

export default function EditAccountDialog({dialog}) {
    return (
        <>
            <FormDialog dialog={dialog}>
                <Form.Item
                    key="name"
                    name="name"
                    label="Name"
                >
                    <Input disabled={true}/>
                </Form.Item>
                <Form.Item
                    key="email"
                    name="email"
                    label="Email"
                >
                    <Input disabled={true}/>
                </Form.Item>
                <Form.Item
                    key="fullName"
                    name="fullName"
                    label="Full name"
                    rules={[
                        {required: true, message: "Full name is required"},
                    ]}
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    key="groups"
                    name="groups"
                    label="Groups"
                >
                    <SelectMultipleAccountGroups/>
                </Form.Item>
            </FormDialog>
        </>
    )
}
