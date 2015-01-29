package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.properties.ValidationRunProperties

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

    String getStatus() {
        validationRunStatuses[0].statusID.id
    }
}
