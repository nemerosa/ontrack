package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.extension.api.DecorationExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.labels.MainBuildLinksFilterService
import net.nemerosa.ontrack.model.labels.MainBuildLinksService
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component
import java.util.*

@Component
class BuildLinkDecorationExtension(
    extensionFeature: GeneralExtensionFeature,
    private val structureService: StructureService,
    private val mainBuildLinksService: MainBuildLinksService,
    private val mainBuildLinksFilterService: MainBuildLinksFilterService,
    private val buildDisplayNameService: BuildDisplayNameService
) : AbstractExtension(extensionFeature), DecorationExtension<BuildLinkDecorationList> {

    override fun getScope(): EnumSet<ProjectEntityType> = EnumSet.of(ProjectEntityType.BUILD)

    override fun getDecorations(entity: ProjectEntity): List<Decoration<BuildLinkDecorationList>> {
        // Gets the main build links of the source project
        val labels = mainBuildLinksService.getMainBuildLinksConfig(entity.project).labels

        // Gets the number of links
        val linksCount = structureService.getCountQualifiedBuildsUsedBy(entity as Build)

        // Gets the main links from this build
        var extraLinks = false
        val mainLinks = structureService.getQualifiedBuildsUsedBy(entity as Build, 0, Int.MAX_VALUE) { link ->
            val mainLink = labels.isEmpty() || mainBuildLinksFilterService.isMainBuidLink(link.build, labels)
            // There are extra links if a target build is NOT a main link
            extraLinks = extraLinks || !mainLink
            // OK
            mainLink
        }.pageItems

        // No main links ==> no decoration at all
        if (mainLinks.isEmpty()) {
            return emptyList()
        } else {
            // Decoration items for the main links
            val decorations = mainLinks.map { getDecoration(it) }
            // Global decoration
            return listOf(
                Decoration.of(
                    this,
                    BuildLinkDecorationList(
                        buildId = entity.id(),
                        linksCount = linksCount,
                        decorations = decorations,
                        extraLink = null,
                    )
                )
            )
        }
    }

    protected fun getDecoration(buildLink: BuildLink): BuildLinkDecoration {
        // Gets the list of promotion runs for this build
        val promotionRuns = structureService.getLastPromotionRunsForBuild(buildLink.build.id)
        // Gets the label to use for the decoration
        val label = buildDisplayNameService.getBuildDisplayName(buildLink.build)
        // Decoration
        return buildLink.asBuildLinkDecoration(promotionRuns, label)
    }

}
