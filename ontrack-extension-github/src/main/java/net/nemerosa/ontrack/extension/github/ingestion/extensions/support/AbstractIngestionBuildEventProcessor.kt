package net.nemerosa.ontrack.extension.github.ingestion.extensions.support

import net.nemerosa.ontrack.extension.github.ingestion.processing.AbstractIngestionEventProcessor
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResultDetails
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.extension.github.ingestion.support.IngestionModelAccessService
import net.nemerosa.ontrack.model.structure.Build

abstract class AbstractIngestionBuildEventProcessor<P : AbstractGitHubIngestionBuildPayload>(
    private val ingestionModelAccessService: IngestionModelAccessService,
) : AbstractIngestionEventProcessor<P>() {

    override fun getPayloadSource(payload: P): String? = payload.getSource()

    override fun process(
        payload: P,
        configuration: String?,
    ): IngestionEventProcessingResultDetails {
        val build = if (payload.buildLabel != null) {
            findBuildByBuildLabel(payload, payload.buildLabel)
        } else if (payload.buildName != null) {
            findBuildByBuildName(payload, payload.buildName)
        } else if (payload.runId != null) {
            findBuildByRunId(payload, payload.runId)
        } else {
            error("Could not find any way to identify a build using $payload")
        }
        return if (build != null) {
            process(build, payload)
        } else {
            IngestionEventProcessingResultDetails.ignored("Cannot find build based on $payload.")
        }
    }

    protected abstract fun process(build: Build, input: P): IngestionEventProcessingResultDetails

    private fun findBuildByRunId(input: P, runId: Long): Build? =
        ingestionModelAccessService.findBuildByRunId(
            repository = Repository.stub(input.owner, input.repository),
            runId = runId,
        )

    private fun findBuildByBuildName(input: P, buildName: String): Build? =
        ingestionModelAccessService.findBuildByBuildName(
            repository = Repository.stub(input.owner, input.repository),
            buildName = buildName,
        )

    private fun findBuildByBuildLabel(input: P, buildLabel: String): Build? =
        ingestionModelAccessService.findBuildByBuildLabel(
            repository = Repository.stub(input.owner, input.repository),
            buildLabel = buildLabel,
        )
}