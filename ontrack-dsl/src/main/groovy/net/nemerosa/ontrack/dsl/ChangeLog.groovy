package net.nemerosa.ontrack.dsl

class ChangeLog extends AbstractResource {

    ChangeLog(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    String getUuid() {
        node['uuid']
    }

}
