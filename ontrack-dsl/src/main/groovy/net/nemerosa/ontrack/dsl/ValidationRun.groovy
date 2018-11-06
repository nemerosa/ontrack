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

    @DSLMethod("Gets the associated run info with this validation run or `null` if none")
    RunInfo getRunInfo() {
        def result = ontrack.get(link("runInfo"))
        def info = new RunInfo(ontrack, result)
        return info.id != 0 ? info : null
    }

    @DSLMethod("Sets the run info for this validation run.")
    void setRunInfo(Map<String, ?> info) {
        ontrack.put(
                link("runInfo"),
                info
        )
    }

    @DSLMethod("Gets the data for the validation run, map with `id` and `data`, or null if not defined.")
    def getData() {
        if (node.data) {
            return [
                    id  : node.data.descriptor.id,
                    data: node.data.data
            ]
        } else {
            return null
        }
    }
}
