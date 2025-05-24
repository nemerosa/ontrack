import {useQuery} from "@components/services/GraphQL";
import {gql} from "graphql-request";

export const useGlobalRoles = () => {
    const {data: globalRoles, loading} = useQuery(
        gql`
            query GlobalRoles {
                globalRoles {
                    id
                    name
                    description
                }
            }
        `,
        {
            initialData: [],
            dataFn: data => data.globalRoles,
        }
    )
    return {globalRoles, loading}
}
