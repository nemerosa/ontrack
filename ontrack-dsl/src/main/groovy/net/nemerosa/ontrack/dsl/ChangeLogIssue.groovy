package net.nemerosa.ontrack.dsl

class ChangeLogIssue extends AbstractResource {

    ChangeLogIssue(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    String getKey() {
        node['key']
    }

    String getDisplayKey() {
        node['displayKey']
    }

    String getSummary() {
        node['summary']
    }

    String getUrl() {
        node['url']
    }

    String getStatus() {
        node['status']?.name
    }

    String getUpdateTime() {
        node['updateTime']
    }

}
