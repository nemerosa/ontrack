package net.nemerosa.ontrack.extension.config.model

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfig
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.structure.PromotionLevelConfiguration

@APIDescription("Branch configuration")
data class BranchConfiguration(
    override val properties: List<PropertyConfiguration> = emptyList(),
    val validations: List<ValidationStampConfiguration> = emptyList(),
    val promotions: List<PromotionLevelConfiguration> = emptyList(),
    val autoVersioning: AutoVersioningConfig? = null,
) : PropertiesConfiguration {
    @JsonIgnore
    fun isNotEmpty(): Boolean =
        properties.isNotEmpty() || validations.isNotEmpty() || promotions.isNotEmpty() || autoVersioning != null

    fun merge(branch: BranchConfiguration) = BranchConfiguration(
        properties = this.properties + branch.properties,
        validations = this.validations + branch.validations,
        promotions = this.promotions + branch.promotions,
        autoVersioning = this.autoVersioning?.merge(branch.autoVersioning),
    )


    private fun AutoVersioningConfig?.merge(autoVersioning: AutoVersioningConfig?): AutoVersioningConfig? =
        if (this == null) {
            autoVersioning
        } else if (autoVersioning == null) {
            this
        } else {
            AutoVersioningConfig(
                configurations = this.configurations + autoVersioning.configurations,
            )
        }

}
