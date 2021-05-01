package net.nemerosa.ontrack.dsl.v4

import net.nemerosa.ontrack.dsl.v4.doc.DSL
import net.nemerosa.ontrack.dsl.v4.doc.DSLMethod

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

    @DSLMethod("Is this account disabled?")
    boolean isDisabled() {
        return node.disabled
    }

    @DSLMethod("Is this account locked?")
    boolean isLocked() {
        return node.locked
    }

    @DSLMethod("Disables this account")
    void disable() {
        ontrack.checkNoUserError(
                ontrack.graphQLQuery('''
                    mutation DisableAccount($id: Int!) {
                        disableAccount(input: {id: $id}) {
                            errors {
                                message
                            }
                        }
                    }
                ''', [id: id]),
                "disableAccount"
        )
    }

    @DSLMethod("Enables this account")
    void enable() {
        ontrack.checkNoUserError(
                ontrack.graphQLQuery('''
                    mutation EnableAccount($id: Int!) {
                        enableAccount(input: {id: $id}) {
                            errors {
                                message
                            }
                        }
                    }
                ''', [id: id]),
                "enableAccount"
        )
    }

    @DSLMethod("Locks this account")
    void lock() {
        ontrack.checkNoUserError(
                ontrack.graphQLQuery('''
                    mutation LockAccount($id: Int!) {
                        lockAccount(input: {id: $id}) {
                            errors {
                                message
                            }
                        }
                    }
                ''', [id: id]),
                "lockAccount"
        )
    }

    @DSLMethod("Unlocks this account")
    void unlock() {
        ontrack.checkNoUserError(
                ontrack.graphQLQuery('''
                    mutation UnlockAccount($id: Int!) {
                        unlockAccount(input: {id: $id}) {
                            errors {
                                message
                            }
                        }
                    }
                ''', [id: id]),
                "unlockAccount"
        )
    }

}
