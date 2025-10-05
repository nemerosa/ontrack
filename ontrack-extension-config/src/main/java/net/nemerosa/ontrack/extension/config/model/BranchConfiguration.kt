package net.nemerosa.ontrack.extension.config.model

import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Branch configuration")
data class BranchConfiguration(
    override val properties: List<PropertyConfiguration> = emptyList(),
    val validations: List<ValidationStampConfiguration> = emptyList(),
    val promotions: List<PromotionLevelConfiguration> = emptyList(),
) : PropertiesConfiguration {
    fun isNotEmpty(): Boolean = properties.isNotEmpty() || validations.isNotEmpty() || promotions.isNotEmpty()
    fun merge(branch: BranchConfiguration) = BranchConfiguration(
        properties = this.properties + branch.properties,
        validations = this.validations + branch.validations,
        promotions = this.promotions + branch.promotions,
    )
}
