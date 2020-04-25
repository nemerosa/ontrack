package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.doc.DSL
import net.nemerosa.ontrack.dsl.doc.DSLMethod

@DSL("Management of tokens")
class TokenManagement {

    private final Ontrack ontrack

    TokenManagement(Ontrack ontrack) {
        this.ontrack = ontrack
    }

    @DSLMethod("Generates a token for the current user")
    Token generate() {
        def response = ontrack.post("rest/tokens/new", null)
        if (response.token) {
            return new Token(
                    response.token.value as String
            )
        } else {
            throw new IllegalStateException("Could not generate a token.")
        }
    }

}
