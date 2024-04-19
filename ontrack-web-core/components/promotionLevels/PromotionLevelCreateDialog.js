import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import PromotionLevelFormItemDescription from "@components/promotionLevels/PromotionLevelFormItemDescription";
import PromotionLevelFormItemName from "@components/promotionLevels/PromotionLevelFormItemName";
import {EventsContext} from "@components/common/EventsContext";
import {gqlPromotionLevelFragment} from "@components/services/fragments";
import {gql} from "graphql-request";
import {useContext} from "react";

export const usePromotionLevelCreateDialog = () => {

    const eventsContext = useContext(EventsContext)

    return useFormDialog({
        prepareValues: (values, {branch}) => {
            return {
                ...values,
                branchId: branch.id,
            }
        },
        query: gql`
            mutation CreatePromotionLevel(
                $branchId: Int!,
                $name: String!,
                $description: String!,
            ) {
                createPromotionLevelById(input: {
                    branchId: $branchId,
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
        userNode: 'createPromotionLevelById',
        onSuccess: (createPromotionLevelById) => {
            eventsContext.fireEvent("promotionLevel.created", {...createPromotionLevelById.promotionLevel})
        }
    })
}

export default function PromotionLevelCreateDialog({dialog}) {
    return (
        <>
            <FormDialog dialog={dialog}>
                <PromotionLevelFormItemName/>
                <PromotionLevelFormItemDescription/>
            </FormDialog>
        </>
    )
}