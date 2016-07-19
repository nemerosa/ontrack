package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.doc.DSL

@DSL("Account group. Just a name and a description.")
class AccountGroup extends AbstractResource {

    AccountGroup(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    @DSL("Unique ID for the group.")
    int getId() {
        node['id'] as int
    }

    @DSL("Name of the group. Unique.")
    String getName() {
        node['name']
    }

    @DSL("Description of the group.")
    String getDescription() {
        node['description']
    }

}
