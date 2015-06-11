package net.nemerosa.ontrack.dsl

class ChangeLogFile extends AbstractResource {

    ChangeLogFile(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    String getPath() {
        node['path']
    }

    List<String> getChangeTypes() {
        node['changeTypes'] as List
    }

    String getChangeType() {
        changeTypes.last()
    }

}
