import {backend, getAccessToken} from "@/app/api/protected/backend";
import {NextResponse} from "next/server";

export const download = async ({uri}) => {
    const accessToken = await getAccessToken()
    if (!accessToken) {
        return NextResponse.json({error: "Unauthorized"}, {status: 401})
    }

    const backendUrl = `${backend.url}/${uri}`

    const backendResponse = await fetch(backendUrl, {
        method: 'GET',
        headers: {
            Authorization: `Bearer ${accessToken}`
        },
    })

    if (!backendResponse.ok) {
        return NextResponse.json({error: backendResponse.statusText}, {status: backendResponse.status})
    }

    const contentDisposition = backendResponse.headers.get('content-disposition')
    const contentType = backendResponse.headers.get('content-type')

    const readableStream = backendResponse.body

    return new Response(readableStream, {
        status: 200,
        headers: {
            'Content-Type': contentType,
            'Content-Disposition': contentDisposition,
        },
    })
}