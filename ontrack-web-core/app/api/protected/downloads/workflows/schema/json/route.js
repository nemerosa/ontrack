import {download} from "@/app/api/protected/downloads/downloads";

export async function GET() {
    return await download({
        uri: 'extension/workflows/download/schema/json'
    })
}