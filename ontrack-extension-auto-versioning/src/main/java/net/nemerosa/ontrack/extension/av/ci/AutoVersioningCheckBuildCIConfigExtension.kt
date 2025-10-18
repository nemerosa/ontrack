package net.nemerosa.ontrack.extension.av.ci

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.av.AutoVersioningExtensionFeature
import net.nemerosa.ontrack.extension.av.validation.AutoVersioningValidationService
import net.nemerosa.ontrack.extension.config.extensions.CIConfigExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

@Component
class AutoVersioningCheckBuildCIConfigExtension(
    autoVersioningExtensionFeature: AutoVersioningExtensionFeature,
    private val autoVersioningValidationService: AutoVersioningValidationService,
) : AbstractExtension(autoVersioningExtensionFeature), CIConfigExtension<Boolean> {

    override val id: String = "autoVersioningCheck"

    override fun parseData(data: JsonNode): Boolean = data.asBoolean()

    override fun mergeData(defaults: Boolean, custom: Boolean): Boolean = custom

    override val projectEntityTypes: Set<ProjectEntityType> = setOf(ProjectEntityType.BUILD)

    override fun configure(
        entity: ProjectEntity,
        data: Boolean
    ) {
        if (data) {
            autoVersioningValidationService.checkAndValidate(entity as Build)
        }
    }
}