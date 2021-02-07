package net.nemerosa.ontrack.dsl.v4

import net.nemerosa.ontrack.dsl.v4.doc.DSL
import net.nemerosa.ontrack.dsl.v4.doc.DSLMethod

@DSL("Authentication source for an account - indicates how the account is authenticated: LDAP, built-in, etc.")
class AuthenticationSource extends AbstractResource {

    AuthenticationSource(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    @DSLMethod("Identifier for the source provider: ldap, built-in, oidc, etc.")
    String getProvider() {
        return node.provider as String
    }

    @DSLMethod("Identifier for the source itself. Might be blank.")
    String getKey() {
        return node.key as String
    }

    @DSLMethod("Display name for the source")
    String getName() {
        return node.name as String
    }

    @DSLMethod("Does this source allow to change the password?")
    boolean isAllowingPasswordChange() {
        return node.allowingPasswordChange as boolean
    }
}
