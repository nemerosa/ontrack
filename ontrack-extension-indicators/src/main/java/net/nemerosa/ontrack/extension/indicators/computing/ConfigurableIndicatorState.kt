package net.nemerosa.ontrack.extension.indicators.computing

import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Stored state for a [configurable indicator][ConfigurableIndicatorType].
 *
 * @property enabled Is this indicator enabled?
 * @property link Link to the description of this indicator
 * @property values Values for this indicator
 */
class ConfigurableIndicatorState(
    @APIDescription("Is this indicator enabled?")
    val enabled: Boolean,
    @APIDescription("Link to a description for this indicator")
    val link: String?,
    @APIDescription("List of attribute values for this indicator")
    val values: List<ConfigurableIndicatorAttributeValue>
) {
    /**
     * Gets the value for an attribute
     */
    fun getAttribute(key: String): String? =
        values.find { it.attribute.key == key }?.value

    /**
     * Gets the value for an attribute as an int
     */
    fun getIntAttribute(key: String): Int? =
        getAttribute(key)?.toInt()

    /**
     * Gets the value for an attribute of type [ConfigurableIndicatorAttributeType.REQUIRED].
     *
     * Returns `true` when the value is strictly equal to `"true"` else returns `false`.
     */
    fun getRequiredAttribute(): Boolean =
        values.find { it.attribute.type == ConfigurableIndicatorAttributeType.REQUIRED}
            ?.value
            ?.toBooleanStrictOrNull()
            ?: false

    companion object {
        fun toAttributeList(
            type: ConfigurableIndicatorType<*, *>,
            values: Map<String, String?>
        ): List<ConfigurableIndicatorAttributeValue> =
            type.attributes.map { attribute ->
                ConfigurableIndicatorAttributeValue(
                    attribute = attribute,
                    value = values[attribute.key]
                )
            }
    }
}