import {gql} from "graphql-request";
import {useQuery} from "@components/services/GraphQL";

/**
 * Resolves the promotion level a chart widget is configured with, by name.
 *
 * Returns the loaded object as `promotionLevelObject`, `null` until the query has answered, and
 * `notFound` once it has answered with nothing: the configured project, branch or promotion level
 * does not exist (any more), or is not visible to the user. A failed query is neither loaded nor
 * not found, so that a network error is never reported as a deleted entity.
 *
 * The shape is shared with `useValidationStampByName`, so that the widgets tell the three states
 * apart the same way on both families (#1694).
 */
export const usePromotionLevel = (project, branch, promotionLevel) => {
    const {data, error, finished} = useQuery(
        gql`
            query GetPromotionLevelByName(
                $project: String!,
                $branch: String!,
                $promotionLevel: String!,
            ) {
                promotionLevelByName(project: $project, branch: $branch, name: $promotionLevel) {
                    id
                    name
                    description
                    image
                    branch {
                        id
                        name
                        displayName
                        project {
                            id
                            name
                        }
                    }
                }
            }
        `,
        {
            variables: {project, branch, promotionLevel},
            condition: !!(project && branch && promotionLevel),
            deps: [project, branch, promotionLevel],
            initialData: null,
            dataFn: data => data.promotionLevelByName,
        }
    )
    return {
        promotionLevelObject: data,
        notFound: finished && !error && !data,
    }
}
