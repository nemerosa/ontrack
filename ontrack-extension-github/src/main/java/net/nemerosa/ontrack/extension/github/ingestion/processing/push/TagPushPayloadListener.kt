package net.nemerosa.ontrack.extension.github.ingestion.processing.push

import net.nemerosa.ontrack.extension.general.ReleaseProperty
import net.nemerosa.ontrack.extension.general.ReleasePropertyType
import net.nemerosa.ontrack.extension.git.property.GitCommitPropertyType
import net.nemerosa.ontrack.extension.github.ingestion.support.IngestionModelAccessService
import net.nemerosa.ontrack.model.structure.BuildSearchForm
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

/**
 * Listens to a tag being pushed, and creates a release property for the builds associated with the tag commit.
 */
@Component
class TagPushPayloadListener(
    private val propertyService: PropertyService,
    private val structureService: StructureService,
    private val ingestionModelAccessService: IngestionModelAccessService,
) : PushPayloadListener {

    /**
     * We'll process this payload if and only if the payload is a [tag][PushPayload.getTag].
     */
    override fun preProcessCheck(payload: PushPayload): PushPayloadListenerCheck =
        if (payload.getTag() != null) {
            PushPayloadListenerCheck.TO_BE_PROCESSED
        } else {
            PushPayloadListenerCheck.IGNORED
        }

    override fun process(payload: PushPayload, configuration: String?) {
        // Gets the tag name (it's already been checked in the precheck)
        val tag = payload.getTag() ?: error("Cannot process tag event because tag is missing.")
        // Gets the head commit (it's already been checked in the precheck)
        val commit = payload.headCommit?.id ?: error("Cannot process tag event because head commit is missing.")
        // Gets the project targeted by the payload
        val project = ingestionModelAccessService.getOrCreateProject(payload.repository, configuration)
        // Gets all builds having the commit as a property
        val builds = structureService.buildSearch(
            project.id,
            BuildSearchForm(
                maximumCount = 10, // Hardcoded, unlikely we'd need more
                property = GitCommitPropertyType::class.java.name,
                propertyValue = commit,
            )
        )
        // Setting the release property for these builds, if not already available
        builds.forEach { build ->
            if (!propertyService.hasProperty(build, ReleasePropertyType::class.java)) {
                propertyService.editProperty(
                    build,
                    ReleasePropertyType::class.java,
                    ReleaseProperty(name = tag),
                )
            }
        }
    }
}