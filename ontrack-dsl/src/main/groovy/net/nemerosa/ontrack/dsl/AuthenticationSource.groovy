package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.doc.DSL

@DSL("Authentication source for an account - indicates how the account is authenticated: LDAP, built-in, etc.")
class AuthenticationSource extends AbstractResource {

    AuthenticationSource(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    @DSL("Identifier for the source: ldap, password")
    String getId() {
        return node.id as String
    }

    @DSL("Display name for the source")
    String getName() {
        return node.name as String
    }

    @DSL("Does this source allow to change the password?")
    boolean isAllowingPasswordChange() {
        return node.allowingPasswordChange as boolean
    }
}
