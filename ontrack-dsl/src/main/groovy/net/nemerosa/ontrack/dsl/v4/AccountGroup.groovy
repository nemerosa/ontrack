package net.nemerosa.ontrack.dsl.v4

import net.nemerosa.ontrack.dsl.v4.doc.DSL
import net.nemerosa.ontrack.dsl.v4.doc.DSLMethod

@DSL("Account group. Just a name and a description.")
class AccountGroup extends AbstractResource {

    AccountGroup(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    @DSLMethod("Unique ID for the group.")
    int getId() {
        node['id'] as int
    }

    @DSLMethod("Name of the group. Unique.")
    String getName() {
        node['name']
    }

    @DSLMethod("Description of the group.")
    String getDescription() {
        node['description']
    }

}
