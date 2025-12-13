import {GraphQLClient} from "graphql-request";
import {backend, getAccessToken} from "@/app/api/protected/backend";
import {NextResponse} from "next/server";

const graphQLClient = (accessToken) => new GraphQLClient(
    `${backend.url}/graphql`, {
        headers: {
            Authorization: `Bearer ${accessToken}`
        },
    });

export const graphQL = async (request, {query, variables = {}}) => {
    const accessToken = await getAccessToken()
    if (!accessToken) {
        return {
            data: null,
            response: NextResponse.json({error: "Unauthorized"}, {status: 401})
        }
    }

    try {
        const data = await graphQLClient(accessToken).request(query, variables)
        return {
            data,
            response: null,
        }
    } catch (error) {
        if (error.code === 'ERR_EXPIRED_ACCESS_TOKEN' || error.response?.status === 401) {
            return {
                data: null,
                response: NextResponse.json({error: "Unauthorized"}, {status: 401})
            }
        } else {
            console.error("GraphQL", {error, query})
            return {
                data: null,
                response: NextResponse.json({error}, {status: 500}),
            }
        }
    }
}

