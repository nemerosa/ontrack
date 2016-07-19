package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.doc.DSL

@DSL("Representation of a user account.")
class Account extends AbstractResource {

    Account(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    @DSL("Unique ID for the account.")
    int getId() {
        return node.id as int
    }

    @DSL("User name, used for signing in.")
    String getName() {
        return node.name
    }

    @DSL("Display name for the account.")
    String getFullName() {
        return node.fullName
    }

    @DSL("Email for the account.")
    String getEmail() {
        return node.email
    }

    @DSL("Source of the account: LDAP, built-in, ...")
    AuthenticationSource getAuthenticationSource() {
        return new AuthenticationSource(ontrack, node.authenticationSource)
    }

    @DSL("Role for the user: admin or not.")
    String getRole() {
        return node.role
    }

    @DSL("List of groups this account belongs to.")
    List<AccountGroup> getAccountGroups() {
        return node.accountGroups.collect {
            new AccountGroup(ontrack, it)
        }
    }

}
