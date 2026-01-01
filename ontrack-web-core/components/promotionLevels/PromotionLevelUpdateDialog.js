import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {getPromotionLevelById, gqlPromotionLevelFragment} from "@components/services/fragments";
import {gql} from "graphql-request";
import {EventsContext} from "@components/common/EventsContext";
import {useContext} from "react";
import PromotionLevelFormItemDescription from "@components/promotionLevels/PromotionLevelFormItemDescription";
import PromotionLevelFormItemName from "@components/promotionLevels/PromotionLevelFormItemName";

export const usePromotionLevelUpdateDialog = () => {

    const client = useGraphQLClient()
    const eventsContext = useContext(EventsContext)

    return useFormDialog({
        init: (form, {id}) => {
            getPromotionLevelById(client, id).then(pl => form.setFieldsValue(pl))
        },
        prepareValues: (values, {id}) => {
            return {
                ...values,
                id: Number(id),
            }
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
                <PromotionLevelFormItemName/>
                <PromotionLevelFormItemDescription/>
            </FormDialog>
        </>
    )
}