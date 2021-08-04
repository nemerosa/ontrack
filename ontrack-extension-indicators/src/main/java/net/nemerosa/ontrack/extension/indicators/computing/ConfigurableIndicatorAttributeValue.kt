package net.nemerosa.ontrack.extension.indicators.computing

/**
 * Attribute value for a [configurable indicator state][ConfigurableIndicatorState].
 *
 * @property attribute Attribute definition
 * @property value Attribute value
 */
class ConfigurableIndicatorAttributeValue(
    val attribute: ConfigurableIndicatorAttribute,
    val value: String?
)
