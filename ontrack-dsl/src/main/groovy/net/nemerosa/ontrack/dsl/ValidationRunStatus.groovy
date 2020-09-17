package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.doc.DSL
import net.nemerosa.ontrack.dsl.doc.DSLMethod

@DSL
class ValidationRunStatus extends AbstractResource {

    ValidationRunStatus(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    @DSLMethod("Returns the numeric ID of this entity.")
    int getId() {
        node['id'] as int
    }

    @DSLMethod("Returns the status ID in JSON form")
    Object getStatusID() {
        return node['statusID']
    }

    @DSLMethod("Returns the status ID in text form")
    String getStatus() {
        return node['statusID']['id'] as String
    }

    @DSLMethod("Returns the status display name")
    String getStatusName() {
        return node['statusID']['name'] as String
    }

    @DSLMethod("Returns the status description")
    String getDescription() {
        return node['description']
    }

    @DSLMethod("Returns if the status is passed or not")
    boolean isPassed() {
        return node['passed'] as boolean
    }

    @DSLMethod("Sets the description on this status")
    void setDescription(String value) {
        ontrack.put(
                link("comment"),
                [ comment: value ]
        )
    }

}
