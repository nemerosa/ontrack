import {GraphQLClient} from "graphql-request";
import {backend} from "@/app/api/protected/backend";
import {getServerSession} from "next-auth";
import {authOptions} from "@/app/api/auth/authOptions";
import {NextResponse} from "next/server";

const graphQLClient = (accessToken) => new GraphQLClient(
    `${backend.url}/graphql`, {
        headers: {
            Authorization: `Bearer ${accessToken}`
        },
    });

export const graphQL = async (request, {query, variables = {}}) => {
    const session = await getServerSession(authOptions)

    if (!session || !session.accessToken) {
        return {
            data: null,
            response: NextResponse.json({error: "Unauthorized"}, {status: 401})
        }
    }

    const accessToken = session.accessToken

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

