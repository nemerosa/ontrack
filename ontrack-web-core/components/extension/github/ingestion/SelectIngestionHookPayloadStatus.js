import {gql} from "graphql-request";
import GraphQLEnumSelect from "@components/common/select/GraphQLEnumSelect";

export default function SelectIngestionHookPayloadStatus({id, value, onChange, mode = "multiple"}) {
    return <GraphQLEnumSelect
        id={id}
        value={value}
        onChange={onChange}
        query={
            gql`
                query SelectIngestionHookPayloadStatus {
                    gitHubIngestionHookPayloadStatuses
                }
            `
        }
        queryNode="gitHubIngestionHookPayloadStatuses"
        mode={mode}
    />
}