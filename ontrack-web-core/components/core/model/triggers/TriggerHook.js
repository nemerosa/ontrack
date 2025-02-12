import {useQuery} from "@components/services/useQuery";
import {gql} from "graphql-request";

export const useTriggers = () => {

    const {data, loading} = useQuery(
        gql`
            query TriggerList {
                triggerList {
                    id
                    displayName
                }
            }
        `,
        {
            dataFn: data => data.triggerList,
        }
    )

    return {
        data,
        loading,
    }
}