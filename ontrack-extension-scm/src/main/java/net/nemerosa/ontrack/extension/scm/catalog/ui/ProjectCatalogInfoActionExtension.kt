package net.nemerosa.ontrack.extension.scm.catalog.ui

import net.nemerosa.ontrack.extension.api.ProjectEntityActionExtension
import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.support.Action
import org.springframework.stereotype.Component
import java.util.*

@Component
class ProjectCatalogInfoActionExtension(
        extensionFeature: SCMExtensionFeature
) : AbstractExtension(extensionFeature), ProjectEntityActionExtension {

    override fun getAction(entity: ProjectEntity): Optional<Action> =
            Optional.of(Action.of(
                    "project-catalog-info",
                    "Project catalog info",
                    "project/${entity.id()}/catalog-info"
            ))

}