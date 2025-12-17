import {graphQLCallMutation} from "@ontrack/graphql";
import {gql} from "graphql-request";

export const setReleaseProperty = async (build, release) => {
    return graphQLCallMutation(
        build.ontrack.connection,
        'setBuildReleasePropertyById',
        gql`
            mutation SetBuildReleasePropertyById(
                $buildId: Int!,
                $release: String!,
            ) {
                setBuildReleasePropertyById(input: {
                    id: $buildId,
                    release: $release,
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
        {
            buildId: Number(build.id),
            release,
        }
    )
}