import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {gql} from "graphql-request";
import PromotionLevelFormItemDescription from "@components/promotionLevels/PromotionLevelFormItemDescription";
import PromotionLevelFormItemName from "@components/promotionLevels/PromotionLevelFormItemName";

export const usePredefinedPromotionLevelUpdateDialog = ({onChange}) => {

    return useFormDialog({
        init: (form, {ppl}) => {
            form.setFieldsValue(ppl)
        },
        prepareValues: (values, {ppl}) => {
            return {
                ...values,
                id: ppl.id,
            }
        },
        query: gql`
            mutation UpdatePredefinedPromotionLevel(
                $id: Int!,
                $name: String!,
                $description: String!,
            ) {
                updatePredefinedPromotionLevel(input: {
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
        userNode: 'updatePredefinedPromotionLevel',
        onSuccess: onChange
    })
}

export default function PredefinedPromotionLevelUpdateDialog({dialog}) {
    return (
        <>
            <FormDialog dialog={dialog}>
                <PromotionLevelFormItemName/>
                <PromotionLevelFormItemDescription/>
            </FormDialog>
        </>
    )
}