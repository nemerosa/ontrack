import {graphQL} from "@/app/api/protected/graphql/graphql";

export async function POST(request) {
    const {query, variables} = await request.json()
    const {data, response} = await graphQL(request, {query, variables})
    return response ?? Response.json(data)
}
