import {GraphQLClient} from "graphql-request";
import {backend} from "@/app/api/protected/backend";

const graphQLClient = (/* accessToken */) => new GraphQLClient(
    `${backend.url}/graphql`, {
        headers: {
            Authorization: `Basic YWRtaW46YWRtaW4=` // TODO admin:admin
            // Authorization: `Bearer ${accessToken}`
        },
    });

export const graphQL = async (request, {query, variables = {}}) => {
    // const session = await getSession()
    //
    // if (!session || !session.user) {
    //     return {
    //         data: null,
    //         response: NextResponse.json({error: "Unauthorized"}, {status: 401}),
    //     }
    // }

    try {
        // const {accessToken} = await getAccessToken(request)
        const data = await graphQLClient(/* accessToken */).request(query, variables)
        return {
            data,
            response: null,
        }
    } catch (error) {
        throw error
        // if (error.code === 'ERR_EXPIRED_ACCESS_TOKEN') {
        //     return {
        //         data: null,
        //         response: Response.redirect(`BASE_URL/api/auth/logout`) // TODO
        //         // TODO response: Response.redirect(`${process.env.AUTH0_BASE_URL}/api/auth/logout`)
        //     }
        // } else {
        //     return {
        //         data: null,
        //         response: NextResponse.json({error: "Unauthorized"}, {status: 401}),
        //     }
        // }
    }
}

