import {backend} from "@/app/api/protected/backend";

export const fetchImageDataURL = async (uri) => {
    const url = `${backend.url}/${uri}`
    const res = await fetch(url, {
        headers: {
            Authorization: `Basic YWRtaW46YWRtaW4=`, // TODO admin:admin
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