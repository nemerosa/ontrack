import {NextResponse} from "next/server";
import {cookieName, cookieOptions, isConnectionLoggingEnabled, ontrackUiUrl, ontrackUrl} from "@/connection";

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

    const path = request.nextUrl.pathname

    if (path.startsWith('/api')) {
        return NextResponse.next() // Only for the health endpoint
    } else if (!path.startsWith('/_next') && !path.startsWith("/ontrack")) {
        const logging = isConnectionLoggingEnabled()
        const cookie = request.cookies.get(cookieName)

        if (cookie) {
            if (logging) console.log(`[connection][middleware][${path}] Cookie is already set`)
            // Already authenticated, going forward
        } else {
            const ontrackUi = ontrackUiUrl()
            if (path.startsWith('/auth')) {
                const token = request.nextUrl.searchParams.get('token')
                const href = request.nextUrl.searchParams.get('href')
                if (logging) console.log(`[connection][middleware][${path}] Auth request, setting the cookie and redirecting`, {
                    token,
                    href
                })
                // Preparing the response
                const response = NextResponse.redirect(href ?? ontrackUi)
                // Setting the cookie
                response.cookies.set(cookieName, token)
                response.cookies.set({
                    ...cookieOptions(),
                    name: cookieName,
                    value: token,
                })
                // OK
                return response
            } else {
                const tokenCallbackHref = `${ontrackUiUrl()}${path}`
                const url = `${ontrackUrl()}/login?token=true&tokenCallback=${ontrackUi}/auth&tokenCallbackHref=${tokenCallbackHref}`
                if (logging) console.log(`[connection][middleware][${path}] Redirecting to login page at ${url}`)
                return NextResponse.redirect(new URL(url))
            }
        }
    }
}