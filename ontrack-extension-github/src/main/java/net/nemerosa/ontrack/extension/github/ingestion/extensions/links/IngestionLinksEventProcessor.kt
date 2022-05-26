package net.nemerosa.ontrack.extension.github.ingestion.extensions.links

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.general.ReleasePropertyType
import net.nemerosa.ontrack.extension.github.ingestion.processing.AbstractIngestionEventProcessor
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventPreprocessingCheck
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResult
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.extension.github.ingestion.support.IngestionModelAccessService
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.BuildSearchForm
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class IngestionLinksEventProcessor(
    private val ingestionModelAccessService: IngestionModelAccessService,
    private val structureService: StructureService,
) : AbstractIngestionEventProcessor<GitHubIngestionLinksPayload>() {

    override fun getPayloadSource(payload: GitHubIngestionLinksPayload): String? =
        payload.getSource()

    override fun preProcessingCheck(payload: GitHubIngestionLinksPayload): IngestionEventPreprocessingCheck =
        if (payload.buildLinks.isNotEmpty()) {
            IngestionEventPreprocessingCheck.TO_BE_PROCESSED
        } else {
            IngestionEventPreprocessingCheck.IGNORED
        }

    override fun process(
        payload: GitHubIngestionLinksPayload,
        configuration: String?,
    ): IngestionEventProcessingResult {
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
            IngestionEventProcessingResult.PROCESSED
        } else {
            IngestionEventProcessingResult.IGNORED
        }
    }

    private fun process(build: Build, input: GitHubIngestionLinksPayload): Build {
        val targets = input.buildLinks.mapNotNull { link ->
            getTargetBuild(link)
        }
        targets.forEach { link ->
            structureService.addBuildLink(build, link)
        }
        // TODO Add only? See StructureService.editBuildLinks
        // OK
        return build
    }

    private fun getTargetBuild(link: GitHubIngestionLink): Build? {
        // Gets the project if possible
        val project = structureService.findProjectByName(link.project).getOrNull() ?: return null
        // Gets the build by label
        return if (link.buildRef.startsWith("#")) {
            val label = link.buildRef.substringAfter("#")
            structureService.buildSearch(
                project.id, BuildSearchForm(
                    maximumCount = 1,
                    property = ReleasePropertyType::class.java.name,
                    propertyValue = label,
                )
            ).firstOrNull()
        }
        // Gets the build by name
        else {
            structureService.buildSearch(
                project.id, BuildSearchForm(
                    maximumCount = 1,
                    buildName = link.buildRef,
                    buildExactMatch = true,
                )
            ).firstOrNull()
        }
    }

    override val payloadType: KClass<GitHubIngestionLinksPayload> =
        GitHubIngestionLinksPayload::class

    override val event: String = EVENT

    private fun findBuildByRunId(input: GitHubIngestionLinksPayload, runId: Long): Build? =
        ingestionModelAccessService.findBuildByRunId(
            repository = Repository.stub(input.owner, input.repository),
            runId = runId,
        )

    private fun findBuildByBuildName(input: GitHubIngestionLinksPayload, buildName: String): Build? =
        ingestionModelAccessService.findBuildByBuildName(
            repository = Repository.stub(input.owner, input.repository),
            buildName = buildName,
        )

    private fun findBuildByBuildLabel(input: GitHubIngestionLinksPayload, buildLabel: String): Build? =
        ingestionModelAccessService.findBuildByBuildLabel(
            repository = Repository.stub(input.owner, input.repository),
            buildLabel = buildLabel,
        )

    companion object {
        const val EVENT = "x-ontrack-build-links"
    }
}