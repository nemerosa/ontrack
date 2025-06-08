import {backend, getAccessToken} from "@/app/api/protected/backend";
import {NextResponse} from "next/server";

export const fetchImageDataURL = async (uri) => {
    const accessToken = await getAccessToken()
    if (!accessToken) {
        return {
            data: null,
            response: NextResponse.json({error: "Unauthorized"}, {status: 401})
        }
    }

    const url = `${backend.url}/${uri}`
    const res = await fetch(url, {
        headers: {
            Authorization: `Bearer ${accessToken}`,
        }
    })

    if (res.ok) {
        const arrayBuffer = await res.arrayBuffer()
        const base64String = btoa(
            new Uint8Array(arrayBuffer)
                .reduce((data, byte) => data + String.fromCharCode(byte), '')
        )
        return `data:image/png;base64,${base64String}`
    } else {
        throw new Error(res.statusText)
    }
}

export const putImageData = async (uri, data) => {
    const accessToken = await getAccessToken()
    if (!accessToken) {
        return {
            data: null,
            response: NextResponse.json({error: "Unauthorized"}, {status: 401})
        }
    }

    const url = `${backend.url}/${uri}`
    return await fetch(url, {
        method: 'PUT',
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
        body: data,
    })
}
