import {useMutation, useQuery} from "@components/services/GraphQL";
import {gql} from "graphql-request";

export const useGlobalPermissions = ({refreshState}) => {
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
            deps: [refreshState],
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

export const useMutationAddGlobalPermissionToAccount = ({onSuccess}) => {
    const {mutate, loading} = useMutation(
        gql`
            mutation AddGlobalPermissionToAccount(
                $accountId: Int!,
                $role: String!,
            ) {
                grantGlobalRoleToAccount(input: {
                    accountId: $accountId,
                    globalRole: $role,
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
        {
            userNodeName: 'grantGlobalRoleToAccount',
            onSuccess,
        }
    )

    const addGlobalPermissionToAccount = async ({accountId, role}) => {
        await mutate({
            accountId: Number(accountId),
            role,
        })
    }

    return {addGlobalPermissionToAccount, loading}
}

export const useMutationAddGlobalPermissionToAccountGroup = ({onSuccess}) => {
    const {mutate, loading} = useMutation(
        gql`
            mutation AddGlobalPermissionToAccountGroup(
                $accountGroupId: Int!,
                $role: String!,
            ) {
                grantGlobalRoleToAccountGroup(input: {
                    accountGroupId: $accountGroupId,
                    globalRole: $role,
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
        {
            userNodeName: 'grantGlobalRoleToAccountGroup',
            onSuccess,
        }
    )

    const addGlobalPermissionToAccountGroup = async ({accountGroupId, role}) => {
        await mutate({
            accountGroupId: Number(accountGroupId),
            role,
        })
    }

    return {addGlobalPermissionToAccountGroup, loading}
}

export const useMutationDeleteGlobalPermissionAccount = ({onSuccess}) => {
    const {mutate, loading} = useMutation(
        gql`
            mutation DeleteGlobalPermissionAccount(
                $accountId: Int!
            ) {
                deleteGlobalRoleFromAccount(input: {
                    accountId: $accountId,
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
        {
            userNodeName: 'deleteGlobalRoleFromAccount',
            onSuccess,
        }
    )

    const deleteGlobalPermissionAccount = async ({accountId}) => {
        await mutate({
            accountId: Number(accountId),
        })
    }

    return {deleteGlobalPermissionAccount, loading}
}

export const useMutationDeleteGlobalPermissionAccountGroup = ({onSuccess}) => {
    const {mutate, loading} = useMutation(
        gql`
            mutation DeleteGlobalPermissionAccountGroup(
                $accountGroupId: Int!
            ) {
                deleteGlobalRoleFromAccountGroup(input: {
                    accountGroupId: $accountGroupId,
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
        {
            userNodeName: 'deleteGlobalRoleFromAccountGroup',
            onSuccess,
        }
    )

    const deleteGlobalPermissionAccountGroup = async ({accountGroupId}) => {
        await mutate({
            accountGroupId: Number(accountGroupId),
        })
    }

    return {deleteGlobalPermissionAccountGroup, loading}
}
