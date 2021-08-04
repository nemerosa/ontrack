package net.nemerosa.ontrack.extension.indicators.computing

/**
 * List of supported attribute types for [configurable indicators][ConfigurableIndicatorType].
 *
 * @property displayName Display name for this type
 */
enum class ConfigurableIndicatorAttributeType(
    val displayName: String
) {

    /**
     * Integer type
     */
    INT(displayName = "Integer"),

    /**
     * Regular expression type
     */
    REGEX(displayName = "Regular expression"),

}
