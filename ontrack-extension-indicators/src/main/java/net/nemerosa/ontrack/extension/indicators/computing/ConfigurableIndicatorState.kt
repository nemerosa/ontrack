package net.nemerosa.ontrack.extension.indicators.computing

/**
 * Stored state for a [configurable indicator][ConfigurableIndicatorType].
 *
 * @property enabled Is this indicator enabled?
 * @property link Link to the description of this indicator
 * @property values Values for this indicator
 */
class ConfigurableIndicatorState(
    val enabled: Boolean,
    val link: String?,
    val values: List<ConfigurableIndicatorAttributeValue>
) {
    /**
     * Gets the value for an attribute
     */
    fun getAttribute(key: String): String? =
        values.find { it.attribute.key == key }?.value

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