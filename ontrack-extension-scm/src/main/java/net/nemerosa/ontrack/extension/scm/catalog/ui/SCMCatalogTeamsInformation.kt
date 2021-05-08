package net.nemerosa.ontrack.extension.scm.catalog.ui

import net.nemerosa.ontrack.extension.api.EntityInformationExtension
import net.nemerosa.ontrack.extension.api.model.EntityInformation
import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import net.nemerosa.ontrack.extension.scm.catalog.CatalogLinkService
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ProjectEntity
import org.springframework.stereotype.Component
import java.util.*

/**
 * Displays the list of SCM teams an Ontrack project belongs to.
 */
@Component
class SCMCatalogTeamsInformation(
    extensionFeature: SCMExtensionFeature,
    private val catalogLinkService: CatalogLinkService
) : AbstractExtension(extensionFeature), EntityInformationExtension {

    override fun getInformation(entity: ProjectEntity): Optional<EntityInformation> =
        if (entity is Project) {
            Optional.ofNullable(
                catalogLinkService.getSCMCatalogEntry(entity)
                    ?.run {
                        teams
                    }
                    ?.run {
                        EntityInformation(this@SCMCatalogTeamsInformation, this)
                    }
            )
        } else {
            Optional.empty()
        }

}