import {graphQLCallMutation} from "@ontrack/graphql";
import {gql} from "graphql-request";

export const createBuildLink = async (source, target, qualifier = "") => {
    // Creating a link
    await graphQLCallMutation(
        source.ontrack.connection,
        'linkBuildById',
        gql`
            mutation CreateBuildLink(
                $fromBuildId: Int!,
                $toBuildId: Int!,
                $qualifier: String,
            ) {
                linkBuildById(input: {
                    fromBuild: $fromBuildId,
                    toBuild: $toBuildId,
                    qualifier: $qualifier,
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
        {
            fromBuildId: source.id,
            toBuildId: target.id,
            qualifier,
        }
    )
    // Allowing the chaining
    return source
}