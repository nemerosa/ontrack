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
) : AbstractExtension(extensionFeature), DecorationExtension<BuildLinkDecoration> {

    override fun getScope() = EnumSet.of(ProjectEntityType.BUILD)

    override fun getDecorations(entity: ProjectEntity): List<Decoration<BuildLinkDecoration>> {
        // Gets the main build links of the source project
        val labels = mainBuildLinksService.getMainBuildLinksConfig(entity.project).labels
        // Gets the links from
        return structureService.getBuildLinksFrom(entity as Build)
                // Filters on the labels
                .filter { target ->
                    mainBuildLinksFilterService.isMainBuidLink(target, labels)
                }
                .map { getDecoration(it) }
    }

    protected fun getDecoration(build: Build): Decoration<BuildLinkDecoration> {
        // Gets the list of promotion runs for this build
        val promotionRuns = structureService.getLastPromotionRunsForBuild(build.id)
        // Gets the label to use for the decoration
        val displayProperty: BuildLinkDisplayProperty? = propertyService.getProperty(build.project, BuildLinkDisplayPropertyType::class.java).value
        val labelProperty: ReleaseProperty? = propertyService.getProperty(build, ReleasePropertyType::class.java).value
        val label = displayProperty.getLabel(build, labelProperty)
        // Decoration
        return Decoration.of(this, build.asBuildLinkDecoration(uriBuilder, promotionRuns, label))
    }

}
