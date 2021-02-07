package net.nemerosa.ontrack.dsl.v4

import net.nemerosa.ontrack.dsl.v4.doc.DSL
import net.nemerosa.ontrack.dsl.v4.doc.DSLMethod
import net.nemerosa.ontrack.dsl.v4.properties.ValidationRunProperties

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
        validationRunStatuses[0].status
    }

    @DSLMethod("Gets the associated validation stamp (JSON)")
    def getValidationStamp() {
        node.validationStamp
    }

    @DSLMethod("Gets the list of statuses")
    List<ValidationRunStatus> getValidationRunStatuses() {
        node.validationRunStatuses.collect { node ->
            new ValidationRunStatus(ontrack, node)
        }
    }

    @DSLMethod("Gets the last of statuses")
    ValidationRunStatus getLastValidationRunStatus() {
        validationRunStatuses[0]
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
