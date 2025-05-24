import {useQuery} from "@components/services/GraphQL";
import {gql} from "graphql-request";

export const useGlobalPermissions = () => {
    const {data: globalPermissions, loading} = useQuery(
        gql`
            query GlobalPermissions {
                globalPermissions {
                    target {
                        type
                        id
                        name
                        description
                    }
                    role {
                        id
                        name
                        description
                    }
                }
            }
        `,
        {
            initialData: [],
            dataFn: data => data.globalPermissions,
        }
    )
    return {globalPermissions, loading}
}

export const usePermissionTargets = ({token}) => {
    const {data: permissionTargets, loading} = useQuery(
        gql`
            query PermissionTargets($token: String = null) {
                permissionTargets(token: $token) {
                    type
                    id
                    name
                    description
                }
            }
        `,
        {
            variables: {token},
            deps: [token],
            initialData: [],
            dataFn: data => data.permissionTargets,
        }
    )
    return {permissionTargets, loading}
}