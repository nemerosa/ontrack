package net.nemerosa.ontrack.extension.scm.catalog.ui

import net.nemerosa.ontrack.extension.api.DecorationExtension
import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import net.nemerosa.ontrack.extension.scm.catalog.CatalogLinkService
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Decoration
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component
import java.util.*

/**
 * Decoration for a project which has no SCM entry associated.
 */
@Component
class SCMCatalogEntryOrphanProjectDecoration(
        extensionFeature: SCMExtensionFeature,
        private val catalogLinkService: CatalogLinkService
) : AbstractExtension(extensionFeature), DecorationExtension<SCMCatalogEntryOrphanProjectDecorationData> {

    override fun getDecorations(entity: ProjectEntity): List<Decoration<SCMCatalogEntryOrphanProjectDecorationData>> =
            if (entity is Project && catalogLinkService.isOrphan(entity)) {
                listOf(
                        Decoration.of(
                                this,
                                SCMCatalogEntryOrphanProjectDecorationData(entity.id())
                        )
                )
            } else {
                emptyList()
            }

    override fun getScope(): EnumSet<ProjectEntityType> = EnumSet.of(ProjectEntityType.PROJECT)
}

/**
 * @property id Project ID
 */
data class SCMCatalogEntryOrphanProjectDecorationData(
        val id: Int
)