import GraphQLEnumSelect from "@components/common/select/GraphQLEnumSelect";
import {gql} from "graphql-request";

export default function SelectConfiguration({configurationType, id, value, onChange}) {
    return (
        <>
            <GraphQLEnumSelect
                id={id}
                value={value}
                onChange={onChange}
                query={
                    gql`
                        query SelectConfiguration($configurationType: String!) {
                            configurations(configurationType: $configurationType) {
                                name
                            }
                        }
                    `
                }
                queryVariables={{configurationType}}
                queryNode="configurations"
                entryValue={entry => entry.name}
                entryLabel={entry => entry.name}
            />
        </>
    )
}