import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";

export const useDeleteSubscription = () => {

    const client = useGraphQLClient()

    const deleteSubscription = async ({entity, name}) => {
        await client.request(
            gql`
                mutation DeleteSubscription(
                    $id: String!,
                    $projectEntity: ProjectEntityIDInput,
                ) {
                    deleteSubscription(input: {
                        id: $id,
                        projectEntity: $projectEntity,
                    }) {
                        errors {
                            message
                        }
                    }
                }
            `,
            {
                id: name,
                projectEntity: entity,
            }
        )
    }

    return {
        deleteSubscription,
    }
}