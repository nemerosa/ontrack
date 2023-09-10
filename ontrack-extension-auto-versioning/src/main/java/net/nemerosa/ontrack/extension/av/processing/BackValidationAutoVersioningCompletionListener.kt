package net.nemerosa.ontrack.extension.av.processing

import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component

@Component
class BackValidationAutoVersioningCompletionListener(
    private val structureService: StructureService,
) : AutoVersioningCompletionListener {

    override fun onAutoVersioningCompletion(order: AutoVersioningOrder, outcome: AutoVersioningProcessingOutcome) {
        if (!order.sourceBackValidation.isNullOrBlank() && order.sourceBuildId != null) {
            val sourceBuild = structureService.findBuildByID(ID.of(order.sourceBuildId))
            if (sourceBuild != null) {
                structureService.setupValidationStamp(
                    sourceBuild.branch,
                    order.sourceBackValidation,
                    "Back validation from auto versioning"
                )
                structureService.newValidationRun(
                    build = sourceBuild,
                    validationRunRequest = ValidationRunRequest(
                        validationStampName = order.sourceBackValidation,
                        validationRunStatusId = when (outcome) {
                            AutoVersioningProcessingOutcome.CREATED -> ValidationRunStatusID.STATUS_PASSED
                            else -> ValidationRunStatusID.STATUS_FAILED
                        },
                        description = """
                            This build was pushed for auto versioning in ${order.branch.project.name}/${order.branch.name}.
                        """.trimIndent()
                    )
                )
            }
        }
    }

}