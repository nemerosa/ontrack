package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.doc.DSL
import net.nemerosa.ontrack.dsl.doc.DSLMethod

@DSL("Representation of a user account.")
class Account extends AbstractResource {

    Account(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    @DSLMethod("Unique ID for the account.")
    int getId() {
        return node.id as int
    }

    @DSLMethod("User name, used for signing in.")
    String getName() {
        return node.name
    }

    @DSLMethod("Display name for the account.")
    String getFullName() {
        return node.fullName
    }

    @DSLMethod("Email for the account.")
    String getEmail() {
        return node.email
    }

    @DSLMethod("Source of the account: LDAP, built-in, ...")
    AuthenticationSource getAuthenticationSource() {
        return new AuthenticationSource(ontrack, node.authenticationSource)
    }

    @DSLMethod("Role for the user: admin or not.")
    String getRole() {
        return node.role
    }

    @DSLMethod("List of groups this account belongs to.")
    List<AccountGroup> getAccountGroups() {
        return node.accountGroups.collect {
            new AccountGroup(ontrack, it)
        }
    }

}
