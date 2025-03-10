import GridCell from "@components/grid/GridCell";
import {useQuery} from "@components/services/useQuery";
import {gql} from "graphql-request";
import {gqlSlotDataNoProject, gqlSlotPipelineDataNoBuild} from "@components/extension/environments/EnvironmentGraphQL";

export default function BuildContentEnvironments({build}) {

    const {data, loading} = useQuery(
        gql`
            ${gqlSlotPipelineDataNoBuild}
            ${gqlSlotDataNoProject}
            query BuildEnvironments(
                $id: Int!,
            ) {
                build(id: $id) {
                    slots {
                        ...SlotDataNoProject
                        pipelines(buildId: $id) {
                            pageItems {
                                ...SlotPipelineDataNoBuild
                            }
                        }
                    }
                }
            }
        `,
        {
            variables: {id: build.id},
        }
    )

    return (
        <>
            <GridCell id="environments" title="Environments" loading={loading} padding={true}>
                TODO
            </GridCell>
        </>
    )
}