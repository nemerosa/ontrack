package net.nemerosa.ontrack.extension.indicators.computing

/**
 * List of supported attribute types for [configurable indicators][ConfigurableIndicatorType].
 *
 * @property displayName Display name for this type
 * @property mapping Mapping function (see [map])
 */
enum class ConfigurableIndicatorAttributeType(
    val displayName: String,
    val mapping: (value: String?) -> String = { it ?: "" },
) {

    /**
     * Integer type
     */
    INT(displayName = "Integer"),

    /**
     * Regular expression type
     */
    REGEX(displayName = "Regular expression"),

    /**
     * Required flag
     */
    REQUIRED(displayName = "Required flag", mapping = {
        if (it == "true") {
            "MUST"
        } else {
            "SHOULD"
        }
    });

    /**
     * Mapping the value of an attribute of this type to a value in a representation string.
     */
    fun map(value: String?): String = mapping(value)

}
