import {gql} from "graphql-request";
import GraphQLEnumSelect from "@components/common/select/GraphQLEnumSelect";

export default function SelectIngestionEventProcessingResult({id, value, onChange}) {
    return <GraphQLEnumSelect
        id={id}
        value={value}
        onChange={onChange}
        query={
            gql`
                query SelectIngestionEventProcessingResult {
                    gitHubIngestionEventProcessingResults
                }
            `
        }
        queryNode="gitHubIngestionEventProcessingResults"
    />
}