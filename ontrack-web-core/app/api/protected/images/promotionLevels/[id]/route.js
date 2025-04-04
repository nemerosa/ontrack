import {NextResponse} from "next/server";
import {fetchImageDataURL} from "@/app/api/protected/images/images";

export async function GET(request, {params}) {
    if (params.id) {
        try {
            const dataURL = await fetchImageDataURL(`rest/structure/promotionLevels/${params.id}/image`)
            return NextResponse.json({dataURL})
        } catch (error) {
            return NextResponse.json({error}, {status: 500})
        }
    } else {
        return NextResponse.json({error: "Missing image ID"}, {status: 400})
    }
}
