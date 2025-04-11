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
                    scope: "openid profile email",
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
    providers.push(
        KeycloakProvider({
            id: "keycloak",
            name: "Keycloak",
            clientId: process.env.NEXTAUTH_CLIENT_ID,
            clientSecret: process.env.NEXTAUTH_CLIENT_SECRET,
            issuer: process.env.NEXTAUTH_ISSUER,
        })
    )
}

export const authOptions = {
    providers: providers,
    callbacks: {
        async jwt({token, account}) {
            if (account) {
                token.accessToken = account.access_token
            }
            return token
        },
        async session({session, token}) {
            session.accessToken = token.accessToken
            return session
        }
    },
}
