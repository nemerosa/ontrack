import {NextResponse} from "next/server";

export default function middleware(request) {

    const cookieName = 'ontrack'
    const cookieMs = 30 * 60 * 1000 // 30 minutes
    const cookie = request.cookies.get(cookieName)

    if (cookie) {
        // Already authenticated, going forward
    } else if (request.nextUrl.pathname.startsWith('/auth')) {
        const token = request.nextUrl.searchParams.get('token')
        const href = request.nextUrl.searchParams.get('href')
        // Preparing the response
        const response = NextResponse.redirect(href ?? 'http://localhost:3000')
        // Setting the cookie
        response.cookies.set(cookieName, token)
        response.cookies.set({
            name: cookieName,
            value: token,
            path: '/',
            expires: Date.now() + cookieMs,
        })
        // OK
        return response
    } else {
        return NextResponse.redirect(new URL(`http://localhost:8080/login?targetUrl=test&token=true&tokenCallback=http://localhost:3000/auth&tokenCallbackHref=${request.url}`))
    }
}