package net.nemerosa.ontrack.extension.scm.catalog.ui

import net.nemerosa.ontrack.extension.api.EntityInformationExtension
import net.nemerosa.ontrack.extension.api.model.EntityInformation
import net.nemerosa.ontrack.extension.scm.SCMExtensionConfigProperties
import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import net.nemerosa.ontrack.extension.scm.catalog.CatalogLinkService
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ProjectEntity
import org.springframework.stereotype.Component

/**
 * Displays the list of SCM teams an Ontrack project belongs to.
 */
@Component
class SCMCatalogTeamsInformation(
    extensionFeature: SCMExtensionFeature,
    private val catalogLinkService: CatalogLinkService,
    private val scmExtensionConfigProperties: SCMExtensionConfigProperties,
) : AbstractExtension(extensionFeature), EntityInformationExtension {

    override val title: String = "SCM Teams"

    override fun getInformation(entity: ProjectEntity): EntityInformation? =
        if (entity is Project && scmExtensionConfigProperties.catalog.enabled) {
            catalogLinkService.getSCMCatalogEntry(entity)
                ?.run {
                    teams
                }
                ?.run {
                    EntityInformation(this@SCMCatalogTeamsInformation, this)
                }
        } else {
            null
        }

}