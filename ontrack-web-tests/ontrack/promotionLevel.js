import {generate} from "@ontrack/utils";
import {graphQLCallMutation} from "@ontrack/graphql";
import {gql} from "graphql-request";
import {registerNotificationExtensions} from "@ontrack/extensions/notifications/notifications";

export const createPromotionLevel = async (branch, name) => {
    const actualName = name ?? generate('pl_')

    const data = await graphQLCallMutation(
        branch.ontrack.connection,
        'createPromotionLevelById',
        gql`
            mutation CreatePromotionLevel(
                $branchId: Int!,
                $name: String!,
            ) {
                createPromotionLevelById(input: {
                    branchId: $branchId,
                    name: $name,
                    description: "",
                }) {
                    promotionLevel {
                        id
                        name
                    }
                    errors {
                        message
                    }
                }
            }
        `,
        {
            branchId: Number(branch.id),
            name: actualName,
        }
    )

    return promotionLevelInstance(branch, data.createPromotionLevelById.promotionLevel)
}


const promotionLevelInstance = (branch, data) => {
    const promotionLevel = {
        ontrack: branch.ontrack,
        type: 'PROMOTION_LEVEL',
        ...data,
        branch,
    }

    // Notifications methods
    registerNotificationExtensions(promotionLevel)

    return promotionLevel
}