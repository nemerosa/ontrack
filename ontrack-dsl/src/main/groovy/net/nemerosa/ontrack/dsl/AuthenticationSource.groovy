package net.nemerosa.ontrack.dsl

class AuthenticationSource extends AbstractResource {

    AuthenticationSource(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    String getId() {
        return node.id as String
    }

    String getName() {
        return node.name as String
    }

    boolean isAllowingPasswordChange() {
        return node.allowingPasswordChange as boolean
    }
}
