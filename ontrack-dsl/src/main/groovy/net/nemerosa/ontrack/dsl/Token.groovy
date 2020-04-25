package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.doc.DSL
import net.nemerosa.ontrack.dsl.doc.DSLMethod

@DSL("Representation of a token")
class Token {

    private final String value
    private final Date creation
    private final Date validUntil

    Token(String value, Date creation, Date validUntil) {
        this.value = value
        this.creation = creation
        this.validUntil = validUntil
    }

    @DSLMethod("Value of the token")
    String getValue() {
        return value
    }

    @DSLMethod("Creation timestamp of the token")
    Date getCreation() {
        return creation
    }

    @DSLMethod("End of validity of the token. If null, valid forever until explicitly revoked.")
    Date getValidUntil() {
        return validUntil
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Token token = (Token) o

        if (creation != token.creation) return false
        if (validUntil != token.validUntil) return false
        if (value != token.value) return false

        return true
    }

    int hashCode() {
        int result
        result = value.hashCode()
        result = 31 * result + creation.hashCode()
        result = 31 * result + (validUntil != null ? validUntil.hashCode() : 0)
        return result
    }
}
