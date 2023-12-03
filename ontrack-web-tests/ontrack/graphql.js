import {GraphQLClient} from "graphql-request";

export const graphQLCallMutation = async (connection, userNode, query, variables) => {
    const data = await graphQLCall(connection, query, variables)
    const userData = data[userNode]
    if (userData && userData.errors) {
        throw userData.errors[0]
    } else {
        return data
    }
}

export const graphQLCall = async (connection, query, variables) => {
    const username = connection.credentials.username
    const password = connection.credentials.password

    const token = btoa(`${username}:${password}`)
    const headers = {
        Authorization: `Basic ${token}`,
    }

    const client = new GraphQLClient(
        `${connection.backend}/graphql`,
        {
            headers,
        }
    )

    return client.request(query, variables)
}
