package net.nemerosa.ontrack.dsl

class AccountGroup extends AbstractResource {

    AccountGroup(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    int getId() {
        node['id'] as int
    }

    String getName() {
        node['name']
    }

    String getDescription() {
        node['description']
    }

}
