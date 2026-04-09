import KeycloakProvider from "next-auth/providers/keycloak";

const providers = []
const providerId = process.env.NEXTAUTH_PROVIDER
if (providerId === "oidc") {
    const name = process.env.NEXTAUTH_PROVIDER_NAME ?? "OIDC"
    providers.push(
        {
            id: "oidc",
            name: name,
            type: "oauth",
            wellKnown: `${process.env.NEXTAUTH_ISSUER}/.well-known/openid-configuration`,
            clientId: process.env.NEXTAUTH_CLIENT_ID,
            clientSecret: process.env.NEXTAUTH_CLIENT_SECRET,
            authorization: {
                params: {
                    scope: process.env.NEXTAUTH_SCOPE ?? "openid profile email",
                    audience: encodeURI(process.env.NEXTAUTH_AUDIENCE),
                }
            },
            profile(profile) {
                // Customize user object here if needed
                return {
                    id: profile.sub,
                    name: profile.name,
                    email: profile.email,
                    image: profile.picture,
                };
            },
        },
    )
} else {
    const name = process.env.NEXTAUTH_PROVIDER_NAME ?? "Yontrack"
    const provider = KeycloakProvider({
        id: "keycloak",
        name: name,
        clientId: process.env.NEXTAUTH_CLIENT_ID,
        clientSecret: process.env.NEXTAUTH_CLIENT_SECRET,
        issuer: process.env.NEXTAUTH_ISSUER,
    })
    // When running inside Kubernetes, HTTPS is terminated at the load balancer and
    // back-channel calls from the pod to the public HTTPS URL fail with an SSL error.
    // NEXTAUTH_ISSUER_INTERNAL points to the in-cluster HTTP Keycloak service so that
    // server-side discovery (token exchange, JWKS) uses HTTP, while NEXTAUTH_ISSUER is
    // still used for id_token `iss` claim validation and browser-visible redirects.
    if (process.env.NEXTAUTH_ISSUER_INTERNAL) {
        provider.wellKnown = `${process.env.NEXTAUTH_ISSUER_INTERNAL}/.well-known/openid-configuration`
    }
    providers.push(provider)
}

const baseAuthOptions = {
    providers: providers,
    // pages: {
    //     signIn: '/auth/signin'
    // },
    callbacks: {
        async jwt({token, account}) {
            // Initial login — save all tokens
            if (account) {
                return {
                    ...token,
                    accessToken: account.access_token,
                    refreshToken: account.refresh_token,
                    expiresAt: account.expires_at,
                }
            }

            // Token still valid — return as-is
            if (Date.now() < token.expiresAt * 1000) {
                return token
            }

            // Token expired — refresh
            if (!token.refreshToken) {
                console.error("Token expired but no refresh token available")
                return {...token, error: "RefreshTokenError"}
            }
            console.log("Access token expired, refreshing...")
            try {
                const issuerForDiscovery = process.env.NEXTAUTH_ISSUER_INTERNAL || process.env.NEXTAUTH_ISSUER
                const discoveryRes = await fetch(`${issuerForDiscovery}/.well-known/openid-configuration`)
                const {token_endpoint: tokenEndpoint} = await discoveryRes.json()

                const body = new URLSearchParams({
                    client_id: process.env.NEXTAUTH_CLIENT_ID,
                    client_secret: process.env.NEXTAUTH_CLIENT_SECRET,
                    grant_type: "refresh_token",
                    refresh_token: token.refreshToken,
                    scope: process.env.NEXTAUTH_SCOPE,
                })

                const response = await fetch(tokenEndpoint, {
                    method: "POST",
                    headers: {"Content-Type": "application/x-www-form-urlencoded"},
                    body: body.toString(),
                })

                const newTokens = await response.json()
                if (!response.ok) throw newTokens

                const newExpiresAt = Math.floor(Date.now() / 1000) + newTokens.expires_in
                console.log("Token refreshed successfully, new expiry:", new Date(newExpiresAt * 1000).toISOString())
                return {
                    ...token,
                    accessToken: newTokens.access_token,
                    expiresAt: newExpiresAt,
                    refreshToken: newTokens.refresh_token ?? token.refreshToken,
                }
            } catch (error) {
                console.error("Token refresh failed:", error)
                return {...token, error: "RefreshTokenError"}
            }
        },
        async session({session, token}) {
            session.accessToken = token.accessToken
            session.refreshToken = token.refreshToken
            session.error = token.error
            return session
        }
    },
}

if (process.env.YONTRACK_UI_AUTH_SIGNIN_CUSTOM === 'true') {
    console.log("Using custom signin page")
    baseAuthOptions.pages = {
        signIn: '/auth/signin'
    }
}

export const authOptions = baseAuthOptions
