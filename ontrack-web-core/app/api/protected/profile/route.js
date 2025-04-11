import {NextResponse} from "next/server";

export async function GET() {
    return NextResponse.json({
        auth: {
            account: {
                url: process.env.NEXTAUTH_ACCOUNT_URL
            }
        }
    })
}
