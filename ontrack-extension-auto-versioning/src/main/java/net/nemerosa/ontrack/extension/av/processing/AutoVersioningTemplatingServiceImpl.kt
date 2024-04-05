package net.nemerosa.ontrack.extension.av.processing

import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.extension.scm.changelog.ChangeLogTemplatingService
import net.nemerosa.ontrack.extension.scm.changelog.ChangeLogTemplatingServiceConfig
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import net.nemerosa.ontrack.model.exceptions.ProjectNotFoundException
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.templating.TemplatingRenderable
import net.nemerosa.ontrack.model.templating.TemplatingRenderableFieldNotFoundException
import net.nemerosa.ontrack.model.templating.TemplatingRenderableFieldRequiredException
import net.nemerosa.ontrack.model.templating.TemplatingService
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class AutoVersioningTemplatingServiceImpl(
    private val templatingService: TemplatingService,
    private val structureService: StructureService,
    private val buildDisplayNameService: BuildDisplayNameService,
    private val changeLogTemplatingService: ChangeLogTemplatingService,
) : AutoVersioningTemplatingService {

    override fun generatePRInfo(
        order: AutoVersioningOrder,
        currentVersions: Map<String, String>,
    ): AutoVersioningPRInfo {

        val sourceProject: Project by lazy {
            structureService.findProjectByName(order.sourceProject)
                .getOrNull()
                ?: throw ProjectNotFoundException(order.sourceProject)
        }

        val context: Map<String, Any> by lazy {
            mapOf(
                "sourceProject" to sourceProject,
                "targetBranch" to order.branch,
                "PROMOTION" to order.sourcePromotion!!,
                "PATH" to order.targetPaths.first(),
                "PATHS" to order.targetPaths.joinToString(","),
                "PROPERTY" to (order.targetProperty ?: ""),
                "VERSION" to order.targetVersion,
                "av" to AutoVersioningOrderTemplatingRenderable(order, currentVersions, sourceProject),
            )
        }

        val title = if (order.prTitleTemplate.isNullOrBlank()) {
            order.getCommitMessage()
        } else {
            templatingService.render(
                template = order.prTitleTemplate,
                context = context,
                renderer = PlainEventRenderer.INSTANCE,
            )
        }
        val body = if (order.prBodyTemplate.isNullOrBlank()) {
            order.getCommitMessage()
        } else {
            templatingService.render(
                template = order.prBodyTemplate,
                context = context,
                renderer = PlainEventRenderer.INSTANCE,
            )
        }
        return AutoVersioningPRInfo(title, body)
    }

    private inner class AutoVersioningOrderTemplatingRenderable(
        private val order: AutoVersioningOrder,
        private val currentVersions: Map<String, String>,
        private val sourceProject: Project,
    ) : TemplatingRenderable {
        override fun render(field: String?, configMap: Map<String, String>, renderer: EventRenderer): String =
            when (field) {
                "changelog" -> renderChangeLog(order, currentVersions, sourceProject, configMap, renderer)
                null -> throw TemplatingRenderableFieldRequiredException()
                else -> throw TemplatingRenderableFieldNotFoundException(field)
            }

    }

    private fun renderChangeLog(
        order: AutoVersioningOrder,
        currentVersions: Map<String, String>,
        sourceProject: Project,
        configMap: Map<String, String>,
        renderer: EventRenderer
    ): String {
        val empty = ChangeLogTemplatingServiceConfig.emptyValue(configMap)
        return if (order.sourceBuildId != null && !order.sourcePromotion.isNullOrBlank() && order.targetPaths.size == 1) {
            // Only auto versioning with ONE target path is supported for the change logs
            val currentVersion = currentVersions[order.targetPaths.first()] ?: return empty
            // The "to" build of the change log is the build having been promoted
            val toBuild = structureService.getBuild(ID.of(order.sourceBuildId))
            // For the "from" build, we look for this build in the source project with the
            // current version as display name or name
            val fromBuild = getCurrentBuild(currentVersion, sourceProject) ?: return empty
            // Getting the change log
            changeLogTemplatingService.render(
                fromBuild = fromBuild,
                toBuild = toBuild,
                configMap = configMap,
                renderer = renderer,
            )
        } else {
            empty
        }
    }

    private fun getCurrentBuild(currentVersion: String, sourceProject: Project): Build? =
        buildDisplayNameService.findBuildByDisplayName(
            project = sourceProject,
            name = currentVersion,
            onlyDisplayName = false,
        )

}