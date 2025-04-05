import NextAuth from "next-auth"
import KeycloakProvider from "next-auth/providers/keycloak";

const handler = NextAuth({
    providers: [
        // TODO Make this configurable according to the settings
        KeycloakProvider({
            clientId: process.env.NEXTAUTH_CLIENT_ID,
            clientSecret: process.env.NEXTAUTH_CLIENT_SECRET,
            issuer: process.env.NEXTAUTH_ISSUER,
        })
    ]
})

export {handler as GET, handler as POST}
