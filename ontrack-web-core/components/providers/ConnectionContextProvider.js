import {useMemo} from "react";
import {callGraphQL} from "@components/services/GraphQL";

/**
 * @deprecated Use useQuery / useMutation from GraphQL.js
 */
export const useGraphQLClient = () => {
    return useMemo(() => ({
        request: async (query, variables = {}) => {
            return await callGraphQL({query, variables});
        }
    }), [])
}
