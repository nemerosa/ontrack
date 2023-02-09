package net.nemerosa.ontrack.extension.github.ingestion.config.parser

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import net.nemerosa.ontrack.extension.github.ingestion.config.model.IngestionConfig
import net.nemerosa.ontrack.extension.github.ingestion.config.model.IngestionConfig.Companion.V1_VERSION
import net.nemerosa.ontrack.extension.github.ingestion.config.model.IngestionConfig.Companion.V2_VERSION
import net.nemerosa.ontrack.json.getTextField

object ConfigParser {

    private const val FIELD_VERSION = "version"

    private val yamlFactory = YAMLFactory().apply {
        enable(YAMLGenerator.Feature.LITERAL_BLOCK_STYLE)
    }

    private val mapper = ObjectMapper(yamlFactory)

    fun parseYaml(yaml: String): IngestionConfig =
        try {
            val json = mapper.readTree(yaml)
            // Gets the version from the JSON
            val version = json.getTextField(FIELD_VERSION)
            // Gets the parser from the version
            val parser: JsonConfigParser = when {
                version == V1_VERSION -> ConfigV1Parser
                version == V2_VERSION -> ConfigV2Parser
                version.isNullOrBlank() -> ConfigOldParser
                else -> throw ConfigVersionException(version)
            }
            // Parsing
            parser.parse(json)
        } catch (ex: Exception) {
            throw ConfigParsingException(ex)
        }

    /**
     * Renders the [config][ingestion config] as a YAML document.
     */
    fun toYaml(config: IngestionConfig): String =
        mapper.writeValueAsString(config)

}