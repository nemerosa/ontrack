import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {getPromotionLevelById, gqlPromotionLevelFragment} from "@components/services/fragments";
import {Form, Input} from "antd";
import {gql} from "graphql-request";
import {EventsContext} from "@components/common/EventsContext";
import {useContext} from "react";

export const usePromotionLevelUpdateDialog = () => {

    const client = useGraphQLClient()
    const eventsContext = useContext(EventsContext)

    return useFormDialog({
        init: (form, {id}) => {
            getPromotionLevelById(client, id).then(pl => form.setFieldsValue(pl))
        },
        prepareValues: (values, {id}) => {
            values.id = id
        },
        query: gql`
            mutation UpdatePromotionLevel(
                $id: Int!,
                $name: String!,
                $description: String!,
            ) {
                updatePromotionLevelById(input: {
                    id: $id,
                    name: $name,
                    description: $description,
                }) {
                    errors {
                        message
                    }
                    promotionLevel {
                        ...PromotionLevelData
                    }
                }
            }
            ${gqlPromotionLevelFragment}
        `,
        userNode: 'updatePromotionLevelById',
        onSuccess: (updatePromotionLevelById) => {
            eventsContext.fireEvent("promotionLevel.updated", {...updatePromotionLevelById.promotionLevel})
        }
    })
}

export default function PromotionLevelUpdateDialog({promotionLevelUpdateDialog}) {
    return (
        <>
            <FormDialog dialog={promotionLevelUpdateDialog}>
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