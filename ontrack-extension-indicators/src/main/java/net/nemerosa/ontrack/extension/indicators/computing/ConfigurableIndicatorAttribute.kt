package net.nemerosa.ontrack.extension.indicators.computing

import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Definition of an attribute for a [configurable indicator][ConfigurableIndicatorType].
 *
 * @property key Identifier
 * @property name Display name
 * @property type Type of the value
 * @property required If the value required?
 */
class ConfigurableIndicatorAttribute(
    @APIDescription("Identifier for this attribute, used for storage")
    val key: String,
    @APIDescription("Display name for this attribute")
    val name: String,
    @APIDescription("Type of the value")
    val type: ConfigurableIndicatorAttributeType,
    @APIDescription("True is the attribute is required")
    val required: Boolean
) {
    companion object {
        val requiredFlag = ConfigurableIndicatorAttribute(
            "required",
            "Required",
            ConfigurableIndicatorAttributeType.REQUIRED,
            true,
        )
    }
}