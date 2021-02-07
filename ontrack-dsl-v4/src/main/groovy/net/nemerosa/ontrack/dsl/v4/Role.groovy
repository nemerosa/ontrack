package net.nemerosa.ontrack.dsl.v4

class Role extends AbstractResource {

    Role(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    String getId() {
        return node.id as String
    }

    String getName() {
        return node.name as String
    }

    String getDescription() {
        return node.description as String
    }
}
