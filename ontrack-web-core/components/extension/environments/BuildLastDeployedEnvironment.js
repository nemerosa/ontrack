import {useQuery} from "@components/services/useQuery";
import {gql} from "graphql-request";
import LoadingInline from "@components/common/LoadingInline";
import EnvironmentLink from "@components/extension/environments/EnvironmentLink";

/**
 * Given a build, displays the icon of the highest environment this build is currently deployed into.
 *
 * @param build {id} of the build
 */
export default function BuildLastDeployedEnvironment({build}) {

    const {data, loading} = useQuery(
        gql`
            query BuildLastDeployedEnvironmentQuery($id: Int!) {
                build(id: $id) {
                    currentDeployments(qualifier: "") {
                        slot {
                            id
                            environment {
                                id
                                name
                            }
                        }
                    }
                }
            }
        `,
        {
            variables: {id: build.id},
            deps: [build.id],
            dataFn: data => {
                const deployments = data.build.currentDeployments
                if (deployments && deployments.length) {
                    const deployment = deployments[0]
                    return deployment.slot
                } else {
                    return null
                }
            }
        }
    )

    return (
        <>
            <LoadingInline loading={loading} text="">
                {
                    data &&
                    <EnvironmentLink
                        slot={data}
                    />
                }
            </LoadingInline>
        </>
    )
}