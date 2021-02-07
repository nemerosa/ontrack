package net.nemerosa.ontrack.dsl.v4

import net.nemerosa.ontrack.dsl.v4.doc.DSL
import net.nemerosa.ontrack.dsl.v4.doc.DSLMethod

@DSL
class ChangeLogFile extends AbstractResource {

    ChangeLogFile(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    @DSLMethod("Relative path to the file being changed.")
    String getPath() {
        node['path']
    }

    @DSLMethod("List of possible change types. Can be one of: ADDED, MODIFIED, DELETED, RENAMED, COPIED, UNDEFINED")
    List<String> getChangeTypes() {
        node['changeTypes'] as List
    }

    @DSLMethod("Change type for this file. Can be one of: ADDED, MODIFIED, DELETED, RENAMED, COPIED, UNDEFINED")
    String getChangeType() {
        changeTypes.last()
    }

}
