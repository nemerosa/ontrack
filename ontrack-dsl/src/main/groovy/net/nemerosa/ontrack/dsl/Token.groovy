package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.doc.DSL
import net.nemerosa.ontrack.dsl.doc.DSLMethod

@DSL("Representation of a token")
class Token {

    private final String value

    Token(String value) {
        this.value = value
    }

    @DSLMethod("Value of the token")
    String getValue() {
        return value
    }

}
