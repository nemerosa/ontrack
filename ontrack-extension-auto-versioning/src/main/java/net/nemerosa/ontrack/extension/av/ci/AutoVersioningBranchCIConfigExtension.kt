package net.nemerosa.ontrack.extension.av.ci

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.av.AutoVersioningExtensionFeature
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfig
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfigurationService
import net.nemerosa.ontrack.extension.config.extensions.CIConfigExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

@Component
class AutoVersioningBranchCIConfigExtension(
    autoVersioningExtensionFeature: AutoVersioningExtensionFeature,
    private val autoVersioningConfigurationService: AutoVersioningConfigurationService,
) : AbstractExtension(autoVersioningExtensionFeature), CIConfigExtension<AutoVersioningConfig> {

    override val id: String = "autoVersioning"

    override fun parseData(data: JsonNode): AutoVersioningConfig = data.parse()

    override val projectEntityTypes: Set<ProjectEntityType> = setOf(ProjectEntityType.BRANCH)

    override fun configure(
        entity: ProjectEntity,
        data: AutoVersioningConfig
    ) {
        autoVersioningConfigurationService.setupAutoVersioning(entity as Branch, data)
    }
}