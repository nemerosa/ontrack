import {useQuery} from "@components/services/GraphQL";
import {gql} from "graphql-request";

export const useGlobalPermissions = () => {
    const {} = useQuery(
        gql`
            query GlobalPermissions {
                # TODO
            }
        `
    )
}
