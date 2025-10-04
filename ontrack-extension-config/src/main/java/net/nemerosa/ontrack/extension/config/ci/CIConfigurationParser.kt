package net.nemerosa.ontrack.extension.config.ci

import net.nemerosa.ontrack.extension.config.model.ConfigurationInput

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
    fun parseConfig(yaml: String): ConfigurationInput

}