package net.nemerosa.ontrack.extension.indicators.computing

/**
 * Definition of an attribute for a [configurable indicator][ConfigurableIndicatorType].
 *
 * @property key Identifier
 * @property name Display name
 * @property type Type of the value
 * @property required If the value required?
 */
class ConfigurableIndicatorAttribute(
    val key: String,
    val name: String,
    val type: ConfigurableIndicatorAttributeType,
    val required: Boolean
)