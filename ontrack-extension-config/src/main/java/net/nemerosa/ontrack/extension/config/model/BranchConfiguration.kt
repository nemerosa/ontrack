package net.nemerosa.ontrack.extension.config.model

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.extension.config.extensions.CIConfigExtensionService
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.structure.PromotionLevelConfiguration

@APIDescription("Branch configuration")
data class BranchConfiguration(
    override val properties: List<PropertyConfiguration> = emptyList(),
    override val extensions: List<ExtensionConfiguration> = emptyList(),
    val validations: List<ValidationStampConfiguration> = emptyList(),
    val promotions: List<PromotionLevelConfiguration> = emptyList(),
) : PropertiesConfiguration, ExtensionsConfiguration {
    @JsonIgnore
    fun isNotEmpty(): Boolean =
        properties.isNotEmpty() || validations.isNotEmpty() || promotions.isNotEmpty() || extensions.isNotEmpty()

    fun merge(
        branch: BranchConfiguration,
        ciConfigExtensionService: CIConfigExtensionService,
    ) = BranchConfiguration(
        properties = this.properties + branch.properties,
        validations = this.validations + branch.validations,
        promotions = this.promotions + branch.promotions,
        extensions = ciConfigExtensionService.merge(this.extensions, branch.extensions),
    )

}
