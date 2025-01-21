import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {Form, Input} from "antd";
import {gql} from "graphql-request";

export const usePredefinedPromotionLevelCreateDialog = ({onChange}) => {
    return useFormDialog({
        onSuccess: onChange,
        prepareValues: (values) => {
            return {
                ...values,
                description: values.description ?? '',
            }
        },
        query: gql`
            mutation CreatePredefinedPromotionLevel(
                $name: String!,
                $description: String!,
            ) {
                createPredefinedPromotionLevel(input: {
                    name: $name,
                    description: $description,
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
        userNode: 'createPredefinedPromotionLevel',
    })
}

export default function PredefinedPromotionLevelCreateDialog({dialog}) {
    return (
        <>
            <FormDialog dialog={dialog}>
                <Form.Item
                    name="name"
                    label="Name"
                    rules={[
                        {required: true, message: "Name is required"},
                    ]}
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
