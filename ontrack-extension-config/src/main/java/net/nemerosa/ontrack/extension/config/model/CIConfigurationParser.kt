package net.nemerosa.ontrack.extension.config.model

/**
 * Parsing of a CI configuration.
 */
interface CIConfigurationParser {

    /**
     * Parsing of a CI configuration.
     *
     * @param yaml YAML representation of the configuration, including shortcuts for properties, etc.
     * @return Parsed configuration
     */
    fun parseConfig(yaml: String): RootConfiguration

}