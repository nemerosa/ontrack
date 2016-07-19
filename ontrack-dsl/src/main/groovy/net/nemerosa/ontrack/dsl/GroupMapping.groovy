package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.doc.DSL

@DSL("Mapping between a LDAP group and an account group.")
class GroupMapping extends AbstractResource {

    GroupMapping(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    @DSL("Name of the LDAP group.")
    String getName() {
        node.name
    }

    @DSL("Name of the Ontrack account group.")
    String getGroupName() {
        node.group.name
    }

}
