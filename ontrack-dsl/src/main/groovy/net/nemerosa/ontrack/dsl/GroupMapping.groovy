package net.nemerosa.ontrack.dsl

class GroupMapping extends AbstractResource {

    GroupMapping(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    String getName() {
        node.name
    }

    String getGroupName() {
        node.group.name
    }

}
