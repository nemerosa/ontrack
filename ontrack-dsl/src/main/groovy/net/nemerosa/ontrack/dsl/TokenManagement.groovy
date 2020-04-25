package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.doc.DSL
import net.nemerosa.ontrack.dsl.doc.DSLMethod

@DSL("Management of tokens")
class TokenManagement {

    private final Ontrack ontrack

    TokenManagement(Ontrack ontrack) {
        this.ontrack = ontrack
    }

    @DSLMethod("Getting the current token for the user")
    Token getCurrent() {
        def response = ontrack.get("rest/tokens/current")
        if (response.token) {
            return parseToken(response)
        } else {
            return null
        }
    }

    private static Token parseToken(response) {
        return new Token(
                response.token.value as String,
                Ontrack.parseTimestamp(response.token.creation as String),
                Ontrack.parseTimestamp(response.token.validUntil as String)
        )
    }

    @DSLMethod("Generates a token for the current user")
    Token generate() {
        def response = ontrack.post("rest/tokens/new", null)
        if (response.token) {
            return parseToken(response)
        } else {
            throw new IllegalStateException("Could not generate a token.")
        }
    }

    @DSLMethod("Revokes the token of the current user")
    void revoke() {
        ontrack.post("rest/tokens/revoke", null)
    }

    @DSLMethod("Revokes all tokens")
    void revokeAll() {
        ontrack.post("rest/tokens/revokeAll", null)
    }

}
