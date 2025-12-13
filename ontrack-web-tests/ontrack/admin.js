import {graphQLCall, graphQLCallMutation} from "@ontrack/graphql";
import {gql} from "graphql-request";

class AdminMgt {

    constructor(ontrack) {
        this.ontrack = ontrack
    }

    async createGroup({name, description} = {description: ""}) {
        const data = await graphQLCallMutation(
            this.ontrack.connection,
            'createAccountGroup',
            gql`
                mutation CreateGroup($name: String!, $description: String) {
                    createAccountGroup(input: {
                        name: $name,
                        description: $description,
                    }) {
                        accountGroup {
                            id
                            name
                            description
                        }
                        errors {
                            message
                        }
                    }
                }
            `,
            {name, description}
        )
        return data.accountGroup
    }

    async getGroupByName(groupName) {
        const data = await graphQLCall(
            this.ontrack.connection,
            gql`
                query GetGroupByName($name: String!) {
                    accountGroupByName(name: $name) {
                        id
                        name
                        description
                    }
                }
            `,
            {name: groupName}
        )
        const group = data.accountGroupByName
        if (group) {
            return group
        } else {
            throw new Error(`Cannot find group with name ${groupName}`)
        }
    }

    async mapGroup(idpGroup, groupName) {

        const group = await this.getGroupByName(groupName)

        await graphQLCallMutation(
            this.ontrack.connection,
            'mapGroup',
            gql`
                mutation MapGroup($idpGroup: String!, $groupId: Int!) {
                    mapGroup(input: {
                        idpGroup: $idpGroup,
                        groupId: $groupId,
                    }) {
                        errors {
                            message
                        }
                    }
                }
            `,
            {idpGroup, groupId: Number(group.id)}
        )
    }

    revokeToken = async (tokenName) => {
        await graphQLCallMutation(
            this.ontrack.connection,
            'revokeToken',
            gql`
                mutation RevokeToken($name: String!) {
                    revokeToken(input: {name: $name}) {
                        errors {
                            message
                        }
                    }
                }
            `,
            {name: tokenName}
        )
    }
}

export const admin = (ontrack) => new AdminMgt(ontrack)
