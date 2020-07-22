package net.nemerosa.ontrack.extension.sonarqube.measures

import net.nemerosa.ontrack.extension.api.EntityInformationExtension
import net.nemerosa.ontrack.extension.api.model.EntityInformation
import net.nemerosa.ontrack.extension.sonarqube.SonarQubeExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ProjectEntity
import org.springframework.stereotype.Component

@Component
class SonarQubeMeasuresInformationExtension(
        extensionFeature: SonarQubeExtensionFeature,
        private val sonarQubeMeasuresCollectionService: SonarQubeMeasuresCollectionService
) : AbstractExtension(extensionFeature), EntityInformationExtension {

    override fun getInformation(entity: ProjectEntity): EntityInformation? {
        return if (entity is Build) {
            val measures = sonarQubeMeasuresCollectionService.getMeasures(entity)
            if (measures != null) {
                EntityInformation(this, measures)
            } else {
                null
            }
        } else {
            null
        }
    }
}