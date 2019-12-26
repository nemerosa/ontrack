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

@Component
class SCMCatalogEntryDecorationExtension(
        private val catalogLinkService: CatalogLinkService,
        extensionFeature: SCMExtensionFeature
) : AbstractExtension(extensionFeature), DecorationExtension<SCMCatalogEntryDecorationExtensionData> {

    override fun getDecorations(entity: ProjectEntity): List<Decoration<SCMCatalogEntryDecorationExtensionData>> =
            if (entity is Project) {
                catalogLinkService.getSCMCatalogEntry(entity)?.run {
                    listOf(
                            Decoration.of(
                                    this@SCMCatalogEntryDecorationExtension,
                                    SCMCatalogEntryDecorationExtensionData(
                                            this,
                                            entity.id()
                                    )
                            )
                    )
                } ?: emptyList()
            } else {
                emptyList()
            }

    override fun getScope(): EnumSet<ProjectEntityType> = EnumSet.of(ProjectEntityType.PROJECT)
}