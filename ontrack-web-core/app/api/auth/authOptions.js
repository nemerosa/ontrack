import KeycloakProvider from "next-auth/providers/keycloak";

export const authOptions = {
    providers: [
        // TODO Make this configurable according to the settings
        KeycloakProvider({
            clientId: process.env.NEXTAUTH_CLIENT_ID,
            clientSecret: process.env.NEXTAUTH_CLIENT_SECRET,
            issuer: process.env.NEXTAUTH_ISSUER,
        })
    ],
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
