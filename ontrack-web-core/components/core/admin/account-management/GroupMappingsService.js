import {useMutation, useQuery} from "@components/services/GraphQL";
import {gql} from "graphql-request";

export const useGroupMappings = ({refreshState}) => {
    const {data: groupMappings, loading} = useQuery(
        gql`
            query GroupMappings {
                groupMappings {
                    idpGroup
                    group {
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
            dataFn: data => data.groupMappings,
        }
    )

    return {groupMappings, loading}
}

export const useMutationAddGroupMapping = ({onSuccess}) => {
    const {mutate, loading} = useMutation(
        gql`
            mutation AddGroupMapping(
                $idpGroup: String!,
                $groupId: Int!,
            ) {
                mapGroup(input: {
                    idpGroup: $idpGroup,
                    groupId: $groupId
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
        {
            userNodeName: 'mapGroup',
            onSuccess,
        }
    )

    const addGroupMapping = async ({idpGroup, groupId}) => {
        await mutate({
            idpGroup,
            groupId: Number(groupId),
        })
    }

    return {addGroupMapping, loading}
}

export const useMutationDeleteGroupMapping = ({onSuccess}) => {
    const {mutate, loading} = useMutation(
        gql`
            mutation DeleteGroupMapping(
                $idpGroup: String!,
            ) {
                mapGroup(input: {
                    idpGroup: $idpGroup,
                    groupId: null
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
        {
            userNodeName: 'mapGroup',
            onSuccess,
        }
    )

    const deleteGroupMapping = async ({idpGroup}) => {
        await mutate({
            idpGroup,
        })
    }

    return {deleteGroupMapping, loading}
}
