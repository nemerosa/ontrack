import {NextResponse} from "next/server";

export async function GET() {

    // Forcing the route to be dynamic
    // See https://nextjs.org/docs/13/app/building-your-application/data-fetching/fetching-caching-and-revalidating#multiple-fetch-requests
    export const dynamic = 'force-dynamic'

    return NextResponse.json({
        auth: {
            account: {
                url: process.env.YONTRACK_UI_MANAGE_ACCOUNT_URL
            }
        }
    })
}
