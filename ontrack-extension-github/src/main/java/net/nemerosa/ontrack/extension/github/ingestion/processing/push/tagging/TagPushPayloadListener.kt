package net.nemerosa.ontrack.extension.github.ingestion.processing.push.tagging

import net.nemerosa.ontrack.extension.general.ReleaseProperty
import net.nemerosa.ontrack.extension.general.ReleasePropertyType
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.ConfigService
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.INGESTION_CONFIG_FILE_PATH
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayload
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayloadListener
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayloadListenerCheck
import net.nemerosa.ontrack.extension.github.ingestion.support.IngestionModelAccessService
import net.nemerosa.ontrack.extension.github.ingestion.support.REFS_HEADS_PREFIX
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

/**
 * Listens to a tag being pushed, and creates a release property for the builds associated with the tag commit.
 */
@Component
class TagPushPayloadListener(
    private val propertyService: PropertyService,
    private val ingestionModelAccessService: IngestionModelAccessService,
    private val configService: ConfigService,
    private val taggingStrategyRegistry: TaggingStrategyRegistry,
    private val commitPropertyTaggingStrategy: CommitPropertyTaggingStrategy,
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
        // Gets the project targeted by the payload
        val project = ingestionModelAccessService.getOrCreateProject(payload.repository, configuration)
        // Gets the branch from the base ref if any
        val branch = payload.baseRef?.let {
            ingestionModelAccessService.findBranchByRef(
                project,
                it.removePrefix(REFS_HEADS_PREFIX),
                null
            )
        }

        // If the branch can be found, use the tagging strategies
        val build: Build? = if (branch != null) {
            val config = configService.getOrLoadConfig(branch, INGESTION_CONFIG_FILE_PATH)
            // Gets all the tagging strategies
            val strategies = taggingStrategyRegistry.getTaggingStrategies(config.tagging)
            // Applies them in order to find a proper build to tag
            strategies.map {
                findBuild(branch, payload, it)
            }.firstOrNull()
        }
        // If the branch cannot be found, use the default commit property strategy directly
        else {
            commitPropertyTaggingStrategy.findBuild(null, project, payload)
        }

        if (build != null && !propertyService.hasProperty(build, ReleasePropertyType::class.java)) {
            propertyService.editProperty(
                build,
                ReleasePropertyType::class.java,
                ReleaseProperty(name = tag),
            )
        }
    }

    private fun <C> findBuild(
        branch: Branch,
        payload: PushPayload,
        configuredTaggingStrategy: ConfiguredTaggingStrategy<C>,
    ): Build? =
        configuredTaggingStrategy.findBuild(branch, payload)

}