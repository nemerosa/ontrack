package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.doc.DSL
import net.nemerosa.ontrack.dsl.doc.DSLMethod
import net.nemerosa.ontrack.dsl.properties.ValidationRunProperties

@DSL
class ValidationRun extends AbstractProjectResource {

    ValidationRun(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    def call(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = this
        closure()
    }

    @Override
    ValidationRunProperties getConfig() {
        new ValidationRunProperties(ontrack, this)
    }

    @DSLMethod("Gets the status for this validation run.")
    String getStatus() {
        validationRunStatuses[0].statusID.id
    }

    @DSLMethod("Gets the associated validation stamp (JSON)")
    def getValidationStamp() {
        node.validationStamp
    }

    @DSLMethod("Gets the list of statuses (JSON)")
    def getValidationRunStatuses() {
        node.validationRunStatuses
    }
}
