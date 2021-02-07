package net.nemerosa.ontrack.dsl.v4

import net.nemerosa.ontrack.dsl.v4.doc.DSL
import net.nemerosa.ontrack.dsl.v4.doc.DSLMethod

@DSL("Mapping between a provided group and an account group.")
class GroupMapping extends AbstractResource {

    GroupMapping(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    @DSLMethod("ID of the provided group.")
    int getId() {
        node.id
    }

    @DSLMethod("Name of the provided group.")
    String getName() {
        node.name
    }

    @DSLMethod("Name of the Ontrack account group.")
    String getGroupName() {
        node.group.name
    }

}
