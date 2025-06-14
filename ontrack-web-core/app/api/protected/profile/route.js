import {NextResponse} from "next/server";

export async function GET() {
    return NextResponse.json({
        auth: {
            account: {
                url: process.env.YONTRACK_UI_MANAGE_ACCOUNT_URL
            }
        }
    })
}
