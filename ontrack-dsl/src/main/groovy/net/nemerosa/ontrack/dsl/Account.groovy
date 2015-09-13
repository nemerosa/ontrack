package net.nemerosa.ontrack.dsl

class Account extends AbstractResource {

    Account(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    int getId() {
        return node.id as int
    }

    String getName() {
        return node.name
    }

    String getFullName() {
        return node.fullName
    }

    String getEmail() {
        return node.email
    }

    AuthenticationSource getAuthenticationSource() {
        return new AuthenticationSource(ontrack, node.authenticationSource)
    }

    String getRole() {
        return node.role
    }

    List<AccountGroup> getAccountGroups() {
        return node.accountGroups.collect {
            new AccountGroup(ontrack, it)
        }
    }

}
