package net.nemerosa.ontrack.extension.github.ingestion.extensions.links

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.general.ReleasePropertyType
import net.nemerosa.ontrack.extension.github.ingestion.extensions.support.AbstractIngestionBuildEventProcessor
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventPreprocessingCheck
import net.nemerosa.ontrack.extension.github.ingestion.support.IngestionModelAccessService
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.BuildSearchForm
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class IngestionLinksEventProcessor(
    ingestionModelAccessService: IngestionModelAccessService,
    private val structureService: StructureService,
) : AbstractIngestionBuildEventProcessor<GitHubIngestionLinksPayload>(
    ingestionModelAccessService
) {

    override fun preProcessingCheck(payload: GitHubIngestionLinksPayload): IngestionEventPreprocessingCheck =
        if (payload.buildLinks.isNotEmpty()) {
            IngestionEventPreprocessingCheck.TO_BE_PROCESSED
        } else {
            IngestionEventPreprocessingCheck.IGNORED
        }

    override fun process(build: Build, input: GitHubIngestionLinksPayload) {
        val targets = input.buildLinks.mapNotNull { link ->
            getTargetBuild(link)
        }
        // Gets the existing links
        val existingLinks = structureService.getBuildsUsedBy(build, size = Int.MAX_VALUE).pageItems.map { it.id() }.toMutableSet()
        // Added links
        val addedLinks = mutableSetOf<Int>()
        // Adding all links of the input
        targets.forEach { link ->
            structureService.addBuildLink(build, link)
            addedLinks += link.id()
        }
        // Deletes all authorised links which were not added again
        if (!input.addOnly) {
            // Other links, not authorised to view, were not subject to edition and are not visible
            existingLinks.removeAll(addedLinks)
            existingLinks.forEach { buildId ->
                structureService.deleteBuildLink(
                    build,
                    structureService.getBuild(ID.of(buildId))
                )
            }
        }
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

    companion object {
        const val EVENT = "x-ontrack-build-links"
    }
}