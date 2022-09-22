package net.nemerosa.ontrack.extension.github.autoversioning

import net.nemerosa.ontrack.extension.av.validation.AutoVersioningValidationService
import net.nemerosa.ontrack.extension.github.ingestion.extensions.support.AbstractIngestionBuildEventProcessor
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventPreprocessingCheck
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResultDetails
import net.nemerosa.ontrack.extension.github.ingestion.support.IngestionModelAccessService
import net.nemerosa.ontrack.model.structure.Build
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class AutoVersioningCheckEventProcessor(
    ingestionModelAccessService: IngestionModelAccessService,
    private val autoVersioningValidationService: AutoVersioningValidationService,
) : AbstractIngestionBuildEventProcessor<AutoVersioningCheckDataPayload>(
    ingestionModelAccessService
) {
    override fun process(build: Build, input: AutoVersioningCheckDataPayload): IngestionEventProcessingResultDetails {
        val validationsData = autoVersioningValidationService.checkAndValidate(build)
        return if (validationsData.isEmpty()) {
            IngestionEventProcessingResultDetails.ignored("No validation run was created.")
        } else {
            IngestionEventProcessingResultDetails.processed("${validationsData.size} validation run(s) were created.")
        }
    }

    override fun preProcessingCheck(payload: AutoVersioningCheckDataPayload): IngestionEventPreprocessingCheck =
        IngestionEventPreprocessingCheck.TO_BE_PROCESSED

    override val payloadType: KClass<AutoVersioningCheckDataPayload> = AutoVersioningCheckDataPayload::class

    override val event: String = EVENT

    companion object {
        const val EVENT = "x-ontrack-auto-versioning-check"
    }
}