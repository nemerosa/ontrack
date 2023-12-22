import {NextResponse} from "next/server";
import {
    cookieName,
    cookieOptions,
    isConnectionLoggingEnabled,
    isConnectionTracingEnabled,
    ontrackUiUrl,
    ontrackUrl
} from "@/connection";

export default function middleware(request) {

    const logging = isConnectionLoggingEnabled()
    const tracing = logging && isConnectionTracingEnabled()
    const path = request.nextUrl.pathname
    const query = request.nextUrl.searchParams.toString()

    if (
        !path.startsWith('/_next') &&
        !path.startsWith("/ontrack") &&
        !path.startsWith("/api") &&
        !path.startsWith("/favicon")
    ) {
        if (logging) console.log(`[connection][middleware][${path}] CHECKING`)
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
            const cookie = request.cookies.get(cookieName)
            if (cookie) {
                if (logging) console.log(`[connection][middleware][${path}] Cookie is already set`)
                // Already authenticated, going forward
            } else {
                const ontrackUi = ontrackUiUrl()

                let tokenCallbackHref = `${ontrackUi}${path}`
                if (query) {
                    tokenCallbackHref += `?${query}`
                }
                tokenCallbackHref = encodeURIComponent(tokenCallbackHref)

                const url = `${ontrackUrl()}/login?token=true&tokenCallback=${ontrackUi}/auth&tokenCallbackHref=${tokenCallbackHref}`
                if (logging) console.log(`[connection][middleware][${path}] Redirecting to login page at ${url}`)
                return NextResponse.redirect(new URL(url))
            }
        }
    } else if (tracing) {
        console.log(`[connection][middleware][${path}] PASSTHROUGH`)
    }
}