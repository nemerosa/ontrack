import Auth0Provider from "next-auth/providers/auth0";
import KeycloakProvider from "next-auth/providers/keycloak";

const providers = []
const providerId = process.env.NEXTAUTH_PROVIDER
if (providerId === "auth0") {
    Auth0Provider({
        clientId: process.env.NEXTAUTH_CLIENT_ID,
        clientSecret: process.env.NEXTAUTH_CLIENT_SECRET,
        issuer: process.env.NEXTAUTH_ISSUER,
    })
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
