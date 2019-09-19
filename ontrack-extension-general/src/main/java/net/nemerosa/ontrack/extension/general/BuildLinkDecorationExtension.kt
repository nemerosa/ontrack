package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.extension.api.DecorationExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.labels.MainBuildLinksFilterService
import net.nemerosa.ontrack.model.labels.MainBuildLinksService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.ui.controller.URIBuilder
import org.springframework.stereotype.Component
import java.util.*

@Component
class BuildLinkDecorationExtension(
        extensionFeature: GeneralExtensionFeature,
        private val structureService: StructureService,
        private val uriBuilder: URIBuilder,
        private val propertyService: PropertyService,
        private val mainBuildLinksService: MainBuildLinksService,
        private val mainBuildLinksFilterService: MainBuildLinksFilterService
) : AbstractExtension(extensionFeature), DecorationExtension<BuildLinkDecorationList> {

    override fun getScope(): EnumSet<ProjectEntityType> = EnumSet.of(ProjectEntityType.BUILD)

    override fun getDecorations(entity: ProjectEntity): List<Decoration<BuildLinkDecorationList>> {
        // Gets the main build links of the source project
        val labels = mainBuildLinksService.getMainBuildLinksConfig(entity.project).labels
        // Gets the links from
        val links = structureService.getBuildLinksFrom(entity as Build)
        // Main links
        val mainLinks = links.filter { target ->
            labels.isEmpty() || mainBuildLinksFilterService.isMainBuidLink(target, labels)
        }
        // Checks if there are extra links (besides the main ones)
        val extraLinks = links.size > 0 && (links.size > mainLinks.size)
        val extraLink = if (extraLinks) {
            uriBuilder.getEntityPage(entity)
        } else {
            null
        }
        // No main links, no extra link ==> no decoration at all
        if (mainLinks.isEmpty() && extraLink == null) {
            return emptyList()
        } else {
            // Decoration items for the main links
            val decorations = mainLinks.map { getDecoration(it) }
            // Global decoration
            return listOf(
                    Decoration.of(
                            this,
                            BuildLinkDecorationList(
                                    decorations,
                                    extraLink
                            )
                    )
            )
        }
    }

    protected fun getDecoration(build: Build): BuildLinkDecoration {
        // Gets the list of promotion runs for this build
        val promotionRuns = structureService.getLastPromotionRunsForBuild(build.id)
        // Gets the label to use for the decoration
        val displayProperty: BuildLinkDisplayProperty? = propertyService.getProperty(build.project, BuildLinkDisplayPropertyType::class.java).value
        val labelProperty: ReleaseProperty? = propertyService.getProperty(build, ReleasePropertyType::class.java).value
        val label = displayProperty.getLabel(build, labelProperty)
        // Decoration
        return build.asBuildLinkDecoration(uriBuilder, promotionRuns, label)
    }

}
