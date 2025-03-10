import GridCell from "@components/grid/GridCell";
import {useQuery} from "@components/services/useQuery";
import {gql} from "graphql-request";

export default function BuildContentEnvironments({build}) {

    const {data, loading} = useQuery(
        gql`
            query BuildEnvironments(
                $id: Int!,
            ) {
                build(id: $id) {
                    slots {
                        
                    }
                }
            }
        `
    )

    return (
        <>
            <GridCell id="environments" title="Environments" loading={false} padding={true}>
                TODO
            </GridCell>
        </>
    )
}