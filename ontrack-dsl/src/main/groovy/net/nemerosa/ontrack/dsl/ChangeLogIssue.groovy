package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.doc.DSL
import net.nemerosa.ontrack.dsl.doc.DSLMethod

@DSL
class ChangeLogIssue extends AbstractResource {

    ChangeLogIssue(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    @DSLMethod("Gets the technical key for this issue. For example, `22` for a GitHub issue.")
    String getKey() {
        node['key']
    }

    @DSLMethod("Gets the display key for this issue. For example, `#22` for a GitHub issue.")
    String getDisplayKey() {
        node['displayKey']
    }

    @DSLMethod("Gets the summary for this issue.")
    String getSummary() {
        node['summary']
    }

    @DSLMethod("Gets the URL to this issue.")
    String getUrl() {
        node['url']
    }

    @DSLMethod("Gets the status of this issue.")
    String getStatus() {
        node['status']?.name
    }

    @DSLMethod("Gets the last update time for this issue, as ISO date string.")
    String getUpdateTime() {
        node['updateTime']
    }

}
