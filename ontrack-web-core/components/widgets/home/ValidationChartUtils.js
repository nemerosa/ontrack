import {gql} from "graphql-request";
import {useQuery} from "@components/services/GraphQL";

/**
 * Resolves the validation stamp a chart widget is configured with, by name.
 *
 * Same contract as `usePromotionLevel`: the loaded object or `null`, and `notFound` once the query
 * has answered with nothing. A failed query is neither.
 */
export const useValidationStampByName = (project, branch, validationStamp) => {
    const {data, error, finished} = useQuery(
        gql`
            query GetValidationStampByName(
                $project: String!,
                $branch: String!,
                $validationStamp: String!,
            ) {
                validationStampByName(project: $project, branch: $branch, name: $validationStamp) {
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
            variables: {project, branch, validationStamp},
            condition: !!(project && branch && validationStamp),
            deps: [project, branch, validationStamp],
            initialData: null,
            dataFn: data => data.validationStampByName,
        }
    )
    return {
        validationStampObject: data,
        notFound: finished && !error && !data,
    }
}
