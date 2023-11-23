import {NextResponse} from "next/server";

export default function middleware(request) {
    if (request.nextUrl.pathname.startsWith('/auth')) {
        const token = request.nextUrl.searchParams.get('token')
        const href = request.nextUrl.searchParams.get('href')
        return Response.json(
            {success: false, message: 'Authentication failed'},
            {status: 403}
        )
    } else {
        const cookie = request.cookies.get('ontrack')
        console.log({cookie})
        if (!cookie) {
            return NextResponse.redirect(new URL(`http://localhost:8080/login?targetUrl=test&token=true&tokenCallback=http://localhost:3000/auth&tokenCallbackHref=${request.url}`))
        }
    }
}