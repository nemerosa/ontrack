package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.extension.api.DecorationExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component
import java.util.*

@Component
class BuildLinkDecorationExtension(
    extensionFeature: GeneralExtensionFeature,
    private val structureService: StructureService
) : AbstractExtension(extensionFeature), DecorationExtension<BuildLinkDecorationList> {

    override fun getScope(): EnumSet<ProjectEntityType> = EnumSet.of(ProjectEntityType.BUILD)

    override fun getDecorations(entity: ProjectEntity): List<Decoration<BuildLinkDecorationList>> {
        // Gets the number of links
        val linksCount = structureService.getCountQualifiedBuildsUsedBy(entity as Build)
        // Global decoration
        return listOf(
            Decoration.of(
                this,
                BuildLinkDecorationList(
                    buildId = entity.id(),
                    linksCount = linksCount,
                )
            )
        )
    }

}
