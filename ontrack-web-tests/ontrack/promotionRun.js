import {graphQLCallMutation} from "@ontrack/graphql";
import {gql} from "graphql-request";

const promotionRunFragment = gql`
    fragment PromotionRunData on PromotionRun {
        id
        description
        build {
            id
            name
        }
        promotionLevel {
            id
            name
        }
    }
`

export const createPromotionRun = async (build, promotionLevel, params) => {

    const promotionLevelName = promotionLevel.name
    const description = params?.description

    const data = await graphQLCallMutation(
        build.ontrack.connection,
        'createValidationRunById',
        gql`
            mutation PromoteBuild(
                $buildId: Int!,
                $promotionLevelName: String!,
                $description: String,
            ) {
                createPromotionRunById(input: {
                    buildId: $buildId,
                    promotion: $promotionLevelName,
                    description: $description,
                }) {
                    promotionRun {
                        ...PromotionRunData
                    }
                    errors {
                        message
                    }
                }
            }

            ${promotionRunFragment}
        `,
        {
            buildId: build.id,
            promotionLevelName,
            description,
        }
    )

    return promotionRunInstance(build.ontrack, build, promotionLevel, data.createPromotionRunById.promotionRun)

}

const promotionRunInstance = (ontrack, build, promotionLevel, data) => {
    const run = {
        ontrack,
        ...data,
        build,
        promotionLevel,
    }

    // TODO Promotion run methods

    return run
}
