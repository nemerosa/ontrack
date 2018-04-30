package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.extension.api.EntityInformationExtension
import net.nemerosa.ontrack.extension.api.model.EntityInformation
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component
import java.util.*

@Component
class BuildLinkedFromInformationExtension(
        extensionFeature: GeneralExtensionFeature,
        private val structureService: StructureService
) : AbstractExtension(extensionFeature), EntityInformationExtension {

    override fun getInformation(entity: ProjectEntity): Optional<EntityInformation> {
        if (entity is Build) {
            // Gets the list of builds which are linked TO this build
            val links: List<Build> = structureService.getBuildLinksTo(entity)
            if (links.isEmpty()) {
                return Optional.empty()
            } else {
                // Information
                return Optional.of(
                        EntityInformation(
                                this,
                                links
                        )
                )
            }
        } else {
            return Optional.empty()
        }
    }

}