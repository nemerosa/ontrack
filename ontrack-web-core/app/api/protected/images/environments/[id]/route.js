import {NextResponse} from "next/server";
import {fetchImageDataURL, putImageData} from "@/app/api/protected/images/images";

export async function GET(request, {params}) {
    if (params.id) {
        try {
            const dataURL = await fetchImageDataURL(`rest/extension/environments/environments/${params.id}/image`)
            return NextResponse.json({dataURL})
        } catch (error) {
            return NextResponse.json({error}, {status: 500})
        }
    } else {
        return NextResponse.json({error: "Missing image ID"}, {status: 400})
    }
}

export async function PUT(request, {params}) {
    if (params.id) {
        const data = await request.text()
        return putImageData(`rest/extension/environments/environments/${params.id}/image`, data)
    } else {
        return NextResponse.json({error: "Missing image ID"}, {status: 400})
    }
}
