import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {getValidationStampById, gqlValidationStampFragment} from "@components/services/fragments";
import {Form, Input} from "antd";
import {gql} from "graphql-request";
import {EventsContext} from "@components/common/EventsContext";
import {useContext} from "react";

export const useValidationStampUpdateDialog = () => {

    const client = useGraphQLClient()
    const eventsContext = useContext(EventsContext)

    return useFormDialog({
        init: (form, {id}) => {
            getValidationStampById(client, id).then(pl => form.setFieldsValue(pl))
        },
        prepareValues: (values, {id}) => {
            return {
                ...values,
                id,
            }
        },
        query: gql`
            mutation UpdateValidationStamp(
                $id: Int!,
                $name: String!,
                $description: String!,
            ) {
                updateValidationStampById(input: {
                    id: $id,
                    name: $name,
                    description: $description,
                }) {
                    errors {
                        message
                    }
                    validationStamp {
                        ...ValidationStampData
                    }
                }
            }
            ${gqlValidationStampFragment}
        `,
        userNode: 'updateValidationStampById',
        onSuccess: (updateValidationStampById) => {
            eventsContext.fireEvent("validationStamp.updated", {...updateValidationStampById.validationStamp})
        }
    })
}

export default function ValidationStampUpdateDialog({validationStampUpdateDialog}) {
    return (
        <>
            <FormDialog dialog={validationStampUpdateDialog}>
                <Form.Item
                    name="name"
                    label="Name"
                    rules={[{required: true, message: 'Name is required.'}]}
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    name="description"
                    label="Description"
                >
                    <Input/>
                </Form.Item>
            </FormDialog>
        </>
    )
}