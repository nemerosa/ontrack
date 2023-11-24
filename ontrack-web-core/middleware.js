import {NextResponse} from "next/server";
import {cookieName} from "@/connectionConstants";

export const config = {
    matcher: [
        /*
         * Match all request paths except for the ones starting with:
         * - _next/static (static files)
         * - _next/image (image optimization files)
         * - favicon.ico (favicon file)
         * - ontrack_ (logo)
         */
        '/((?!api|_next/static|_next/image|favicon.ico|ontrack_).*)',
    ],
}

export default function middleware(request) {

    if (!request.nextUrl.pathname.startsWith('/_next')) {
        const cookieMs = 30 * 60 * 1000 // 30 minutes
        const cookie = request.cookies.get(cookieName)

        if (cookie) {
            console.log(`[connection][middleware][${request.nextUrl.pathname}] Cookie set`)
            // Already authenticated, going forward
        } else if (request.nextUrl.pathname.startsWith('/auth')) {
            const token = request.nextUrl.searchParams.get('token')
            const href = request.nextUrl.searchParams.get('href')
            console.log(`[connection][middleware][${request.nextUrl.pathname}] Auth request, setting the cookie and redirecting`, {
                token,
                href
            })
            // Preparing the response
            const response = NextResponse.redirect(href ?? 'http://localhost:3000')
            // Setting the cookie
            response.cookies.set(cookieName, token)
            response.cookies.set({
                name: cookieName,
                value: token,
                path: '/',
                expires: new Date(Date.now() + cookieMs),
            })
            // OK
            return response
        } else {
            console.log(`[connection][middleware][${request.nextUrl.pathname}] Redirecting to login page`)
            return NextResponse.redirect(new URL(`http://localhost:8080/login?targetUrl=test&token=true&tokenCallback=http://localhost:3000/auth&tokenCallbackHref=${request.url}`))
        }
    }
}